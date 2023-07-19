package com.org.recommendvariable;

import com.intellij.openapi.project.Project;

import com.intellij.openapi.ui.Messages;

import javax.naming.directory.InvalidAttributesException;
import java.util.regex.Pattern;

public class VariableMaker {

    public String requestStringValidator(Project currentProject, String requestVariableName) throws Exception {

        requestVariableName = requestVariableName.trim();

        if (requestVariableName.contains(" ")) {
            Messages.showMessageDialog(currentProject, "Please chk to Make Varaiable Name!", "Not Verifiable Statement", Messages.getInformationIcon());
            return "";
        }

        String pattern = "^[가-힣]*$";
        if (!Pattern.matches(pattern, requestVariableName))  {
            Messages.showMessageDialog(currentProject, "only korean languege can do it", "Not Verifiable Statement", Messages.getInformationIcon());
            return "";
        }

        return requestVariableName;
    }

}
