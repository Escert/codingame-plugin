import java.util.Set;

public interface SourceFile {

	String getPackageName();
	Set<String> getImports();
	String getContent();
	String getFileSuffix();
}
