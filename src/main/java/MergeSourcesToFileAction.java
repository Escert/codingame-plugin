import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class MergeSourcesToFileAction extends MergeSourcesAction {

	public MergeSourcesToFileAction() {
		super("Merge sources to file");
	}

	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		Project project = e.getProject();
		MergedFile mergedFile = collectMergedFile(project);

		Path persistencePath = Paths.get(project.getBasePath(), "target");
		File persistedFile = new File(persistencePath.toFile(), "MergedFile" + mergedFile.getFileSuffix());
		System.out.println("Persistence file: "+persistedFile);

		try {
			persistencePath.toFile().mkdirs();
			persistedFile.createNewFile();
			try (FileWriter writer = new FileWriter(persistedFile, false)) {
				writer.append(mergedFile.createFileContent());
			}
			Messages.showMessageDialog(project, "Sources were successfully merged into target file", "Merge Sources", Messages.getInformationIcon());
		} catch(IOException ex) {
			Messages.showMessageDialog(project, "Something went wrong while creating the merged file!" + System.lineSeparator() + ex.getMessage() + System.lineSeparator() + ExceptionUtils.getStackTrace(ex), "Merge Sources", Messages.getErrorIcon());
		}
	}
}
