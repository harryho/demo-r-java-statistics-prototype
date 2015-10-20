package com.rm.app.r.component.data;

import java.util.ArrayList;
import java.util.List;

import com.rm.app.RMAppContext;
import com.rm.app.graph.RMGraph;
import com.rm.app.graph.RMGraphCell;
import com.rm.app.log.RMLogger;

public class RDataCache {

	public static final Integer PROCESS_FAILED_DUPLICATE_DATACOM = -2;
	public static final Integer PROCESS_FAILED_LOADDATA = -1;
	public static final Integer PROCESS_NOT_INIT = 0;
	public static final Integer PROCESS_GOING = 1;
	public static final Integer PROCESS_DONE = 2;

	private Integer status = PROCESS_NOT_INIT;

	private RMGraphCell currentDataCell = null;

	private RDataComponent currentDataCom = null;

	List<String> numericVariables = new ArrayList<String>();

	private String dataName = null;

	private RDataCache() {
	}

	public RDataComponent getCurrentDataCom() {
		return currentDataCom;
	}

	static class Holder {
		static RDataCache instance = new RDataCache();
	}

	public static RDataCache getIntance() {
		return Holder.instance;
	}

	public Integer initial(boolean force) {

		synchronized (status) {
			RMGraph graph = RMAppContext.getGraph();
			List<RMGraphCell> dataComs = graph.getCurrentDataComponents();
			if (dataComs == null || dataComs.size() == 0) {
				cleanCache();
				status = PROCESS_NOT_INIT;
				return status;
			}
			if (dataComs.size() > 1) {
				cleanCache();
				status = PROCESS_FAILED_DUPLICATE_DATACOM;
				return status;
			}

			RMGraphCell dataCell = dataComs.get(0);

			if (currentDataCell != null) {
				if (currentDataCell.getId().equals(dataCell.getId()) && !force) {
					// don't need to load data again.
					return status;
				}
			}
			long begin = System.currentTimeMillis();
			currentDataCell = dataCell;
			// SwingUtilities.invokeLater();
			new DataInitializer(currentDataCell).run();
			long end = System.currentTimeMillis();
			RMLogger.debug("Consume : " + ((end - begin) / 1000) + "s");
		}
		return status;
	}

	/**
	 * Check whether the data has been loaded into cache.
	 * 
	 * @param dataCom
	 * @return
	 */
	public boolean isDataLoaded(RDataComponent dataCom) {
		if (currentDataCom == null) {
			return false;
		}
		if (status == PROCESS_DONE && currentDataCom.equals(dataCom)
				&& numericVariables.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public synchronized int getDataInitailizedStatus() {
		return status.intValue();
	}

	/**
	 * 
	 * @return
	 */
	public final List<String> getAllNumericVariables() {
		synchronized (numericVariables) {
			return RDataHelper.clone(numericVariables);
		}
	}

	/**
	 * 
	 * @param list
	 */
	public void setAllNumericVariables(final List<String> list) {
		synchronized (numericVariables) {
			numericVariables = list;
			if (list != null && list.size() > 0) {
				status = PROCESS_DONE;
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getCurrentDataNameInR() {
		return dataName;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getAllVariables() {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			list.add(i, "Variable " + i);
		}

		return list;
	}

	private void cleanCache() {
		numericVariables = new ArrayList<String>();
		currentDataCell = null;
		dataName = null;
	}

	private class DataInitializer {

		RMGraphCell dataCell;

		DataInitializer(RMGraphCell dataCell) {
			this.dataCell = dataCell;
		}

		public void run() {
			RMLogger.debug("Begin to import data cache...");
			status = processLoadingData();
			RMLogger.debug("status=" + status);
			RMLogger.debug("Finished to import data cache.");
		}

		private Integer processLoadingData() {
			if (dataCell == null) {
				cleanCache();
				return PROCESS_FAILED_LOADDATA;
			}
			RDataComponent newDataCom = RDataHelper
					.buildDataComponent(dataCell);
			if (currentDataCom == null) {
				currentDataCom = newDataCom;
			} else if (isDataLoaded(newDataCom)) {// Don't load again.
				RMLogger.debug("Data component does not changed.");
				return PROCESS_DONE;
			}
			RMLogger.debug("Try to load data into cache.");
			RMLogger.debug("currentDataCom: " + currentDataCom);
			currentDataCom = newDataCom;
			status = PROCESS_GOING;
			if (currentDataCom == null) {
				cleanCache();
				return PROCESS_FAILED_LOADDATA;
			}
			try {
				currentDataCom.validation();
				numericVariables = currentDataCom.getAllNumericVariables();
				if (numericVariables == null || numericVariables.size() == 0) {
					cleanCache();
					return PROCESS_FAILED_LOADDATA;
				}
			} catch (Exception e) {
				e.printStackTrace();
				cleanCache();
				return PROCESS_FAILED_LOADDATA;
			}
			RMLogger.debug("numericVariables: " + numericVariables);
			return PROCESS_DONE;
		}

	}
}
