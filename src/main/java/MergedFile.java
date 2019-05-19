import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MergedFile {

	private final List<SourceFile> sourceFiles;

	public MergedFile(List<SourceFile> sourceFiles) {
		this.sourceFiles = sourceFiles;
	}

	public String createFileContent() {
		StringJoiner content = new StringJoiner(System.lineSeparator());

		determineAllImports().forEach(content::add);
		sourceFiles.stream()
				.map(SourceFile::getContent)
				.forEach(content::add);

		return content.toString();
	}

	public String getFileSuffix() {
		return sourceFiles.get(0).getFileSuffix();
	}

	private Stream<String> determineAllImports() {
		Set<String> projectPackages = determineProjectPackages();
		System.out.println("ProjectPackages: " + projectPackages);

		return sourceFiles.stream()
				.flatMap(sourceFile -> sourceFile.getImports().stream())
				.distinct()
				.filter(importStr -> !isProjectImport(importStr, projectPackages));
	}

	private Set<String> determineProjectPackages() {
		return sourceFiles.stream()
				.map(SourceFile::getPackageName)
				.filter(Objects::nonNull)
				.flatMap(this::listPackageAndParents)
				.collect(Collectors.toSet());
	}

	private Stream<String> listPackageAndParents(String packageName) {
		List<String> packageNames = new ArrayList<>();
		packageNames.add(packageName);

		String currentPackage = packageName;
		while(currentPackage.contains(".")) {
			currentPackage = currentPackage.substring(0, currentPackage.lastIndexOf("."));
			packageNames.add(currentPackage);
		}

		return packageNames.stream();
	}

	private boolean isProjectImport(String importStr, Set<String> projectPackages) {
		String importedPackage = importStr.substring("import ".length(), importStr.lastIndexOf("."));
		return projectPackages.contains(importedPackage);
	}
}
