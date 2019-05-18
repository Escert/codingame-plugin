import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

public class KotlinSourceFile implements SourceFile {

	private final String packageName;
	private final Set<String> imports;
	private final String content;

	public static KotlinSourceFile parse(File sourceFile) {
		try {
			String packageName = null;
			Set<String> imports = new HashSet<>();
			StringJoiner content = new StringJoiner(System.lineSeparator());

			boolean parsingImports = true;
			Scanner scanner = new Scanner(sourceFile, StandardCharsets.UTF_8.name());
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(packageName == null && line.startsWith("package ")) {
					packageName = line.substring("package ".length());
				} else if(parsingImports) {
					if(StringUtils.isBlank(line)) {
						continue;
					} else if(line.startsWith("import ")) {
						imports.add(line);
					} else {
						parsingImports = false;
					}
				}

				if(!parsingImports) {
					content.add(line);
				}
			}

			return new KotlinSourceFile(packageName, imports, content.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private KotlinSourceFile(String packageName, Set<String> imports, String content) {
		this.packageName = packageName;
		this.imports = imports;
		this.content = content;

		System.out.println("Package: " + packageName);
		System.out.println("Imports: " + imports);
		System.out.println("Content: " + content);
	}

	@Override
	public String getPackageName() {
		return packageName;
	}

	@Override
	public Set<String> getImports() {
		return imports;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public String getFileSuffix() {
		return ".kt";
	}
}
