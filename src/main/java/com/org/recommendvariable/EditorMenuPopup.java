package com.org.recommendvariable;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EditorMenuPopup extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Translator translator = new Translator();
        List<String> changed_variable = translator.variableChanger(e);
        if (changed_variable.size() > 1){
            getComboBox(e, changed_variable);
        }
    }

    public void getComboBox(@NotNull AnActionEvent e, List<String> changed_variable) {

        String[] items = changed_variable.toArray(String[]::new);

        Editor editor = e.getDataContext().getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR);
        if (editor == null) return;
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (selectedText == null) return;

        JBPopupFactory.getInstance()
                .createListPopup(new BaseListPopupStep<String>("select variable", items) {
                    @Override
                    public PopupStep<?> onChosen(String selectedValue, boolean finalChoice) {
                        replaceSelectedText(selectedText, selectedValue, editor, e.getProject());
                        return super.onChosen(selectedValue, finalChoice);
                    }
                })
                .showCenteredInCurrentWindow(e.getProject());
    }

    private void replaceSelectedText(String originalText, String replacement, Editor editor, Project project) {
        Runnable runnable = () -> editor.getDocument().replaceString(editor.getSelectionModel().getSelectionStart(), editor.getSelectionModel().getSelectionEnd(), replacement);
        WriteCommandAction.runWriteCommandAction(project, runnable);
    }



}
