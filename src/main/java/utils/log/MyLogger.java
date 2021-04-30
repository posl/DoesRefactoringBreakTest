package utils.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
* Write logs
*/
public class MyLogger {

	/**
	* Design Pattern: Singleton
	*/
	private static MyLogger thisinstance  = null;

	public synchronized static MyLogger getInstance() {
		
		//if there is no instance
		if(MyLogger.thisinstance == null) {
			MyLogger.thisinstance = new MyLogger();
//			BasicConfigurator.configure();
//			MyFileReadWriteUtils.createDirs("logs", false);
			System.setProperty("log4j2.configuration", "resources/log4j2.xml");
	        //System.setProperty("log4j2.configuratorClass", "org.apache.log4j.pj.xml.DOMConfigurator");
//			System.setProperty("org.apache.logging.log4j.pj.simplelog.StatusLogger.level","TRACE");
		}
		return MyLogger.thisinstance;
	}

	/**
	* This is not used because this implementation employs singleton pattern
	*/
	private MyLogger() {}

	private String getMessage(String msg) {
		//get class
		Class<?> c = this.getClass();
		//get class name
		String thisClassName = c.getName();
		//get current thread name
		Thread t = Thread.currentThread();
		//get StackTraceElement arrays
		StackTraceElement[] stackTraceElements = t.getStackTrace();
		int pos = 0;
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			//compare class names
			if(thisClassName.equals(stackTraceElement.getClassName())) {
				break;	//if stackTraceElements is the same name
			}
			pos++;
		}
		pos += 2;		//adjust the position
		StackTraceElement m = stackTraceElements[pos];
		//FORMAT: class name:[method name] + log message
		String log_str = m.getClassName() + ":" + m.getMethodName()+ "() " +msg;
		return log_str;
	}

	/**
	* debug level logging
	* @param msg : message
	*/
	public void debug(String msg) {
		Logger logger = LogManager.getLogger(this.getClass());
		logger.debug("{}", this.getMessage(msg));
	}

	/**
	 * info level logging
	 * @param msg : message
	 */
	public void info(String msg) {
		Logger logger = LogManager.getLogger(this.getClass());
		logger.info("{}", this.getMessage(msg));
	}

	/**
	 * info level logging
	 */
	public void info(Object obj, String msg) {
		Logger logger = LogManager.getLogger(obj.getClass());
		logger.info("{}", msg);
	}

	/**
	 * warn level logging
	 * @param msg : message
	 */
	public void warn(String msg) {
		Logger logger = LogManager.getLogger(this.getClass());
		logger.warn("{}", this.getMessage(msg));
	}

	/**
	* trace level logging
	* @param msg : error message
	*/
	public void trace(String msg) {
		Logger logger = LogManager.getLogger(this.getClass());
		logger.trace("{}", this.getMessage(msg));
	}

	/**
	 * error level logging
	 * @param e    : exception message
	 */
	public void error(Throwable e) {
		String msg = e.getMessage();
		Class<? extends Object> clss = e.getClass();		//Exception class
		String clsname = e.getClass().getName();			//Exception name
		StackTraceElement[] st = e.getStackTrace();
		String log_msg = "";
		if ( st != null && st.length > 0 ) {
			log_msg += "Class:" + clsname+ "¥n";
			log_msg += "Detail:" + msg + "¥n";
			for(int i=0; i<st.length ; i++) {
				String err   = st[i].toString();
				log_msg += err + "¥n";
			}
			Logger logger = LogManager.getLogger(clss);
			logger.error("{}", log_msg);
		}
	}
	/**
	 * error level logging
	 * @param msg    : exception message
	 */
	public void error(String msg) {
		Logger logger = LogManager.getLogger(this.getClass());
		logger.error("{}", this.getMessage(msg));
		System.err.println(msg);
	}
}
		