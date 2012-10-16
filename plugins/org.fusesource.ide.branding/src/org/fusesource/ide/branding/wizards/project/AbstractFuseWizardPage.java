package org.fusesource.ide.branding.wizards.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Combo;
import org.fusesource.ide.branding.Activator;
import org.fusesource.ide.branding.wizards.WizardMessages;



/**
 * AbstractMavenImportWizardPage
 * 
 * @author Eugene Kuleshov
 */
public abstract class AbstractFuseWizardPage extends WizardPage {

  /** the history limit */
  protected static final int MAX_HISTORY = 15;

  /** dialog settings to store input history */
  protected IDialogSettings dialogSettings;

  /** the Map of field ids to List of comboboxes that share the same history */
  private Map<String, List<Combo>> fieldsWithHistory;

  private boolean isHistoryLoaded = false;

  /**
   * Creates a page. This constructor should be used for the wizards where you need to have the advanced settings box on
   * each page. Pass the same bean to each page so they can share the data.
   */
  protected AbstractFuseWizardPage(String pageName) {
    super(pageName);

    fieldsWithHistory = new HashMap<String, List<Combo>>();

    initDialogSettings();
  }

  /** Creates an advanced settings panel. */
/*  protected void createAdvancedSettings(Composite composite, GridData gridData) {
    if(importConfiguration != null) {
//      Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
//      GridData separatorData = new GridData(SWT.FILL, SWT.TOP, false, false, gridData.horizontalSpan, 1);
//      separatorData.verticalIndent = 7;
//      separator.setLayoutData(separatorData);
      gridData.verticalIndent = 7;

      resolverConfigurationComponent = new ResolverConfigurationComponent(composite, importConfiguration, true);
      resolverConfigurationComponent.setLayoutData(gridData);
      addFieldWithHistory("projectNameTemplate", resolverConfigurationComponent.template); //$NON-NLS-1$
    }
  }
*/
  /** Loads the advanced settings data when the page is displayed. */
  public void setVisible(boolean visible) {
    if(visible) {
      if(!isHistoryLoaded) {
        // load data before history kicks in
/*        if(resolverConfigurationComponent != null) {
          resolverConfigurationComponent.loadData();
        }
*/        loadInputHistory();
        isHistoryLoaded = true;
      } else {
        saveInputHistory();
      }
/*      if(resolverConfigurationComponent != null) {
        resolverConfigurationComponent.loadData();
      }
*/    }
    super.setVisible(visible);
  }

  /** Saves the history when the page is disposed. */
  public void dispose() {
    saveInputHistory();
    super.dispose();
  }

  /** Loads the dialog settings using the page name as a section name. */
  private void initDialogSettings() {
    IDialogSettings pluginSettings;
    
    // This is strictly to get SWT Designer working locally without blowing up.
    if( Activator.getDefault() == null ) {
      pluginSettings = new DialogSettings("Workbench");
    }
    else {
      pluginSettings = Activator.getDefault().getDialogSettings();      
    }
    
    dialogSettings = pluginSettings.getSection(getName());
    if(dialogSettings == null) {
      dialogSettings = pluginSettings.addNewSection(getName());
      pluginSettings.addSection(dialogSettings);
    }
  }

  /** Loads the input history from the dialog settings. */
  private void loadInputHistory() {
    for(Map.Entry<String, List<Combo>> e : fieldsWithHistory.entrySet()) {
      String id = e.getKey();
      String[] items = dialogSettings.getArray(id);
      if(items != null) {
        for(Combo combo : e.getValue()) {
          String text = combo.getText();
          combo.setItems(items);
          if(text.length() > 0) {
            // setItems() clears the text input, so we need to restore it
            combo.setText(text);
          }
        }
      }
    }
  }

  /** Saves the input history into the dialog settings. */
  private void saveInputHistory() {
    for(Map.Entry<String, List<Combo>> e : fieldsWithHistory.entrySet()) {
      String id = e.getKey();

      Set<String> history = new LinkedHashSet<String>(MAX_HISTORY);

      for(Combo combo : e.getValue()) {
        String lastValue = combo.getText();
        if(lastValue != null && lastValue.trim().length() > 0) {
          history.add(lastValue);
        }
      }

      Combo combo = e.getValue().iterator().next();
      String[] items = combo.getItems();
      for(int j = 0; j < items.length && history.size() < MAX_HISTORY; j++ ) {
        history.add(items[j]);
      }

      dialogSettings.put(id, history.toArray(new String[history.size()]));
    }
  }

  /** Adds an input control to the list of fields to save. */
  protected void addFieldWithHistory(String id, Combo combo) {
    if(combo != null) {
      List<Combo> combos = fieldsWithHistory.get(id);
      if(combos == null) {
        combos = new ArrayList<Combo>();
        fieldsWithHistory.put(id, combos);
      }
      combos.add(combo);
    }
  }

  protected String validateArtifactIdInput(String text) {
    return validateIdInput(text, true);
  }
  protected String validateGroupIdInput(String text) {
    return validateIdInput(text, false);
  }
  private String validateIdInput(String text, boolean artifact) {
    if(text == null || text.length() == 0) {
      return artifact? WizardMessages.wizardProjectPageMaven2ValidatorArtifactID: WizardMessages.wizardProjectPageMaven2ValidatorGroupID; 
    }

    if(text.contains(" ")) { //$NON-NLS-1$
      return artifact? WizardMessages.wizardProjectPageMaven2ValidatorArtifactIDnospaces: WizardMessages.wizardProjectPageMaven2ValidatorGroupIDnospaces; 
    }

    IStatus nameStatus = ResourcesPlugin.getWorkspace().validateName(text, IResource.PROJECT);
    if(!nameStatus.isOK()) {
      return NLS.bind(artifact? WizardMessages.wizardProjectPageMaven2ValidatorArtifactIDinvalid: WizardMessages.wizardProjectPageMaven2ValidatorGroupIDinvalid, nameStatus.getMessage());
    }

    if(!text.matches("[A-Za-z0-9_\\-.]+")) { //$NON-NLS-1$
      return NLS.bind(artifact? WizardMessages.wizardProjectPageMaven2ValidatorArtifactIDinvalid: WizardMessages.wizardProjectPageMaven2ValidatorGroupIDinvalid, text); 
    }

    return null;
  }
}