package com.rbsfm.plugin.build.publish;
import java.io.File;
import java.util.prefs.Preferences;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.rbsfm.plugin.build.assemble.ProjectAssemblyRequestBuilder;
import com.rbsfm.plugin.build.ivy.Module;
import com.rbsfm.plugin.build.ui.InputWindow;
import com.rbsfm.plugin.build.ui.MessageFormatter;
import com.rbsfm.plugin.build.ui.MessageLogger;
import com.rbsfm.plugin.build.ui.InputWindow.SelectionAdapter;
public class ModulePublicationWindow extends InputWindow{
   private Module module;
   private Text loginField;
   private Text passwordField;
   private Text moduleField;
   private Text revisionField;
   private Text branchField;
   private Text branchRevisionField;
   private Text mailField;
   private Text idField;
   private File file;
   public ModulePublicationWindow(Composite parent,Module module,File file){
      super(parent);
      this.module = module;
      this.file = file;
      createGui();
   }
   private void createModuleDetails(){
      Group entryGroup = new Group(this, SWT.NONE);
      entryGroup.setText("Module Details");
      GridLayout entryLayout = new GridLayout(2, false);
      entryGroup.setLayout(entryLayout);
      entryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      moduleField = createLabelledText(entryGroup, "Module: ", 40, "Enter the module name", module.getModule());
      revisionField = createLabelledText(entryGroup, "Revision: ", 40, "Enter the revision", module.getRevision());
      branchField = createLabelledText(entryGroup, "Branch: ", 40, "Enter the branch", module.getBranch());
      branchRevisionField = createLabelledText(entryGroup, "Branch Revision: ", 40, "Enter the branch revision", module.getBranchRevision());
      mailField = createLabelledText(entryGroup, "Mail: ", 40, "Enter your mail address", null);
   }
   private void createSubversion(){
      Group subversionGroup = new Group(this, SWT.NONE);
      subversionGroup.setText("Subversion");
      GridLayout subversionLayout = new GridLayout(2, false);
      subversionGroup.setLayout(subversionLayout);
      subversionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      loginField = createLabelledText(subversionGroup, "Login: ", 40, "Enter your user name", System.getProperty("user.name"));
      passwordField = createLabelledText(subversionGroup, "Password: ", 40, "Enter your password", null, true);
   }
   private void createJIRA(){
      Group subversionGroup = new Group(this, SWT.NONE);
      subversionGroup.setText("JIRA");
      GridLayout subversionLayout = new GridLayout(2, false);
      subversionGroup.setLayout(subversionLayout);
      subversionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      idField = createLabelledText(subversionGroup, "ID: ", 40, "Enter a JIRA ID", null);
   }
   private void createGui(){
      setLayout(new GridLayout(1, true));
      createModuleDetails();
      createSubversion();
      createJIRA();
      Composite buttons = new Composite(this, SWT.NONE);
      buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      FillLayout buttonLayout = new FillLayout();
      buttonLayout.marginHeight = 2;
      buttonLayout.marginWidth = 2;
      buttonLayout.spacing = 5;
      buttons.setLayout(buttonLayout);
      createButton(buttons, "&Publish", "Publish", new SelectionAdapter(){
         public void widgetSelected(SelectionEvent event){
            String login = loginField.getText();
            String password = passwordField.getText();
            Shell shell = getShell();
            try{
               ModulePublisher publisher = new ModulePublisher(shell, login, password);
               String moduleName = moduleField.getText();
               String revision = revisionField.getText();
               String branch = branchField.getText();
               String branchRevision = branchRevisionField.getText();
               String mailAddress = mailField.getText();
               String id = idField.getText();
               publisher.publish(file, moduleName, revision, branch, branchRevision, mailAddress, id);
            }catch(Exception cause){
               MessageLogger.openInformation(getShell(), "Error", MessageFormatter.format(cause));
               throw new RuntimeException(cause);
            }
         }
      });
      createButton(buttons, "&Clear", "Clear inputs", new SelectionAdapter(){
         public void widgetSelected(SelectionEvent e){
            clearFields();
            moduleField.forceFocus();
         }
      });
      createButton(buttons, "&Server", "Set server location", new SelectionAdapter(){
        public void widgetSelected(SelectionEvent e) {
          Preferences preferences = Preferences.userNodeForPackage(ModulePublicationRequestBuilder.class);
          String server = preferences.get("server", "");
          InputDialog dialog = new InputDialog(getShell(),  "Server location", "Set the location of the publication server", server, null);
          dialog.open();
          String result = dialog.getValue();
          if(result != null && !result.equals("")) {
            preferences.put("server", result);
          }
          moduleField.forceFocus();
        }
      });
   }
}