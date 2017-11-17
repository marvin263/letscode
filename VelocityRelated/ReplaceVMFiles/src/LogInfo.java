import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class LogInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public Map<String, TreeSet<Integer>> mapFullPath2TreeSet = new TreeMap<String, TreeSet<Integer>>();
	public long totalAffectedLines = 0;
	public long lastUsedsequenceNumberInLogFile = 0;
}
