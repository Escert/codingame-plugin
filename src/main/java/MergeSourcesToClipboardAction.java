import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class MergeSourcesToClipboardAction extends MergeSourcesAction {

	public MergeSourcesToClipboardAction() {
		super("Merge sources to clipboard");
	}

	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		Project project = e.getProject();
		MergedFile mergedFile = collectMergedFile(project);

		StringSelection content = new StringSelection(mergedFile.createFileContent());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(content, content);
		Messages.showMessageDialog(project, "Sources were successfully copied to clipboard", "Merge Sources", Messages.getInformationIcon());
	}
}
