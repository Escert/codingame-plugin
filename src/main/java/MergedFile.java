import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MergedFile {

	private final List<SourceFile> sourceFiles;

	public MergedFile(List<SourceFile> sourceFiles) {
		this.sourceFiles = sourceFiles;
	}

	public void persist(Path persistencePath) throws IOException {
		String suffix = sourceFiles.get(0).getFileSuffix();

		File persistedFile = new File(persistencePath.toFile(), "MergedFile" + suffix);
		System.out.println("Persistence file: "+persistedFile);

		try (FileWriter writer = new FileWriter(persistedFile, false)) {
			for(String importStr : determineAllImports()) {
				writer.append(importStr);
				writer.append(System.lineSeparator());
			}

			for(SourceFile sourceFile : sourceFiles) {
				writer.append(sourceFile.getContent());
				writer.append(System.lineSeparator());
			}
		}
	}

	private Set<String> determineAllImports() {
		Set<String> projectPackages = determineProjectPackages();
		System.out.println("ProjectPackages: " + projectPackages);

		return sourceFiles.stream()
				.flatMap(sourceFile -> sourceFile.getImports().stream())
				.distinct()
				.filter(importStr -> !isProjectImport(importStr, projectPackages))
				.collect(Collectors.toSet());
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
