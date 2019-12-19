/*
 * Copyright 2019 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.samples.configure_subnetwork_trace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.utilitynetworks.UtilityAssetGroup;
import com.esri.arcgisruntime.utilitynetworks.UtilityAssetType;
import com.esri.arcgisruntime.utilitynetworks.UtilityAttributeComparisonOperator;
import com.esri.arcgisruntime.utilitynetworks.UtilityCategoryComparison;
import com.esri.arcgisruntime.utilitynetworks.UtilityDomainNetwork;
import com.esri.arcgisruntime.utilitynetworks.UtilityElement;
import com.esri.arcgisruntime.utilitynetworks.UtilityElementTraceResult;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetwork;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetworkAttribute;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetworkAttributeComparison;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetworkSource;
import com.esri.arcgisruntime.utilitynetworks.UtilityTier;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceAndCondition;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceConditionalExpression;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceConfiguration;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceOrCondition;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceParameters;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceResult;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceType;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraversabilityScope;

public class ConfigureSubnetworkTraceController {

  public ComboBox<Boolean> comparisonValuesBooleanComboBox;
  public TextField comparisonValuesNumberField;
  @FXML private ComboBox<CodedValue> comparisonValuesChoicesComboBox;

  @FXML private CheckBox includeBarriersCheckBox;
  @FXML private CheckBox includeContainersCheckBox;
  @FXML private TextArea traceConditionsTextArea;

  @FXML private ComboBox<UtilityNetworkAttribute> comparisonSourcesComboBox;
  @FXML private ComboBox<UtilityAttributeComparisonOperator> comparisonOperatorsComboBox;

  private UtilityNetwork utilityNetwork;
  private UtilityTraceConfiguration initialUtilityTraceConfiguration;
  private UtilityTraceConfiguration utilityTraceConfiguration;
  private UtilityElement startingLocation;

  private UtilityTraceConditionalExpression initialExpression;

  @FXML
  public void initialize() {

    try {
      // load the utility network
      utilityNetwork = new UtilityNetwork(
          "https://sampleserver7.arcgisonline.com/arcgis/rest/services/UtilityNetwork/NapervilleElectric" +
              "/FeatureServer");
      utilityNetwork.loadAsync();
      utilityNetwork.addDoneLoadingListener(() -> {
        if (utilityNetwork.getLoadStatus() == LoadStatus.LOADED) {

          // build the choice list for the network attribute comparison sources
          List<UtilityNetworkAttribute> comparisonSources = utilityNetwork.getDefinition()
              .getNetworkAttributes()
              .stream()
              .filter(value -> !value.isSystemDefined())
              .collect(Collectors.toList());
          comparisonSourcesComboBox.getItems().addAll(comparisonSources);
          comparisonSourcesComboBox.getSelectionModel().select(0);
          // display the name of the comparison sources in the ComboBox
          comparisonSourcesComboBox.setButtonCell(new ComparisonSourceListCell());
          comparisonSourcesComboBox.setCellFactory(c -> new ComparisonSourceListCell());

          // build the choice list for the comparison operators
          comparisonOperatorsComboBox.getItems().addAll(UtilityAttributeComparisonOperator.values());
          comparisonOperatorsComboBox.getSelectionModel().select(0);

          // display the name of the comparison values in the ComboBox
          comparisonValuesChoicesComboBox.setButtonCell(new CodedValueListCell());
          comparisonValuesChoicesComboBox.setCellFactory(c -> new CodedValueListCell());

          // create a default starting location
          UtilityNetworkSource utilityNetworkSource =
              utilityNetwork.getDefinition().getNetworkSource("Electric Distribution Device");
          UtilityAssetGroup utilityAssetGroup = utilityNetworkSource.getAssetGroup("Service Point");
          UtilityAssetType utilityAssetType = utilityAssetGroup.getAssetType("Three Phase Low Voltage Meter");
          startingLocation =
              utilityNetwork.createElement(utilityAssetType, UUID.fromString("3AEC2649-D867-4EA7-965F-DBFE1F64B090"));

          // get a default trace configuration from a tier to update the UI
          UtilityDomainNetwork utilityDomainNetwork =
              utilityNetwork.getDefinition().getDomainNetwork("ElectricDistribution");
          UtilityTier utilityTier = utilityDomainNetwork.getTier("Medium Voltage Radial");
          utilityTraceConfiguration = utilityTier.getTraceConfiguration();

          // save the default trace configuration to restore when the application is reset
          initialUtilityTraceConfiguration = utilityTraceConfiguration;

          // save the initial expression
          initialExpression =
              (UtilityTraceConditionalExpression) utilityTier.getTraceConfiguration().getTraversability().getBarriers();

          // show the initial expression in the text area
          traceConditionsTextArea.setText(generateExpressionText(initialExpression));

          // set the traversability scope
          utilityTier.getTraceConfiguration().getTraversability().setScope(UtilityTraversabilityScope.JUNCTIONS);
        }
      });
    } catch (Exception e) {
      new Alert(Alert.AlertType.ERROR, "Error loading Utility Network.").show();
    }
  }

  /**
   * Uses the selected parameters to add a barrier expression to the utility trace configuration.
   */
  @FXML
  private void onAddConditionClick() {

    // get the selected utility network attribute and attribute comparison operator
    UtilityNetworkAttribute selectedAttribute = comparisonSourcesComboBox.getSelectionModel().getSelectedItem();
    UtilityAttributeComparisonOperator selectedOperator =
        comparisonOperatorsComboBox.getSelectionModel().getSelectedItem();

    // check if a comparison value was specified, and capture it to use as the last parameter of the
    // UtilityNetworkAttributeComparison
    Object otherValue;
    // if a comparison value is selected from the ComboBox, use it as the third parameter
    if (selectedAttribute.getDomain() instanceof CodedValueDomain &&
        comparisonValuesChoicesComboBox.getSelectionModel().getSelectedItem() != null) {
      // convert the selected comparison value to the data type defined by the selected attribute
      otherValue = convertToDataType(comparisonValuesChoicesComboBox.getSelectionModel().getSelectedItem().getCode(),
          selectedAttribute.getDataType());
    } else if (!comparisonValuesNumberField.getText().equals("")) {
      // otherwise, a comparison value will be specified as text input to be used as the third parameter
      otherValue = convertToDataType(comparisonValuesNumberField.getText(), selectedAttribute.getDataType());
    } else {
      new Alert(Alert.AlertType.WARNING, "No valid comparison value entered").show();
      return;
    }

    // create the utility network attribute comparison expression using the specified parameters
    // NOTE: You may also create a UtilityNetworkAttributeComparison with another NetworkAttribute.
    UtilityTraceConditionalExpression expression =
        new UtilityNetworkAttributeComparison(selectedAttribute, selectedOperator, otherValue);

    // check if an expression is already defined for the traversability barriers
    if (utilityTraceConfiguration.getTraversability().getBarriers() instanceof UtilityTraceConditionalExpression) {
      UtilityTraceConditionalExpression otherExpression =
          (UtilityTraceConditionalExpression) utilityTraceConfiguration.getTraversability().getBarriers();
      // use the existing expression to create an `or` expression with the user-defined expression
      expression = new UtilityTraceOrCondition(otherExpression, expression);
    }

    // set the new expression to the traversability
    utilityTraceConfiguration.getTraversability().setBarriers(expression);

    // show the expression in the text area
    traceConditionsTextArea.setText(generateExpressionText(expression));
  }

  /**
   * Parses a utility trace conditional expression into text and returns it.
   * @param expression a UtilityTraceConditionalExpression
   * @return string representing the expression
   */
  private String generateExpressionText(UtilityTraceConditionalExpression expression) {

    StringBuilder stringBuilder = new StringBuilder();

    // for category comparison expressions, add the category name and comparison operator
    if (expression instanceof UtilityCategoryComparison) {
      UtilityCategoryComparison categoryComparison = (UtilityCategoryComparison) expression;
      stringBuilder.append(
          String.format("'%1$s' %2$s", categoryComparison.getCategory().getName(),
              categoryComparison.getComparisonOperator().name()));
    }

    // for network attribute comparison expressions, add the network attribute name and comparison operator
    if (expression instanceof UtilityNetworkAttributeComparison) {
      UtilityNetworkAttributeComparison attributeComparison = (UtilityNetworkAttributeComparison) expression;
      stringBuilder.append(
          String.format("'%1$s' %2$s", attributeComparison.getNetworkAttribute().getName(),
              attributeComparison.getComparisonOperator().name()));

      if (attributeComparison.getNetworkAttribute().getDomain() instanceof CodedValueDomain) {
        CodedValueDomain codedValueDomain = (CodedValueDomain) attributeComparison.getNetworkAttribute().getDomain();

        if (!codedValueDomain.getCodedValues().isEmpty()) {
          // get the data type of the used network attribute comparison
          UtilityNetworkAttribute.DataType attributeComparisonDataType =
              attributeComparison.getNetworkAttribute().getDataType();

          // get the coded values from the domain and find the ones where the value matches the network attribute's
          // comparison value
          List<CodedValue> list = codedValueDomain.getCodedValues()
              .stream()
              .filter(value -> convertToDataType(value.getCode(), attributeComparisonDataType).equals(
                  convertToDataType(attributeComparison.getValue(), attributeComparisonDataType)))
              .collect(Collectors.toList());

          if (!list.isEmpty()) {
            // get the first coded value and add it's name to the string
            CodedValue codedValue = list.get(0);
            stringBuilder.append(String.format(" '%1$s'", codedValue.getName()));
          }
        }

      } else {
        if (attributeComparison.getOtherNetworkAttribute() != null) {
          stringBuilder.append(
              String.format(" '%1$s'", attributeComparison.getOtherNetworkAttribute().getName()));
        } else {
          stringBuilder.append(
              String.format(" '%1$s'", attributeComparison.getValue().toString()));
        }
      }
    }

    // for 'and'/'or' conditions, generate the expression for both sides
    if (expression instanceof UtilityTraceAndCondition) {
      UtilityTraceAndCondition andCondition = (UtilityTraceAndCondition) expression;
      stringBuilder.append(
          String.format("%1$s AND%n %2$s", generateExpressionText(andCondition.getLeftExpression()),
              generateExpressionText(andCondition.getRightExpression())));
    }

    if (expression instanceof UtilityTraceOrCondition) {
      UtilityTraceOrCondition orCondition = (UtilityTraceOrCondition) expression;
      stringBuilder.append(
          String.format("%1$s OR%n %2$s", generateExpressionText(orCondition.getLeftExpression()),
              generateExpressionText(orCondition.getRightExpression())));
    }

    return stringBuilder.toString();
  }

  /**
   * Builds trace parameters using the constructed trace configurations and runs the trace in the utility network. On
   * completion, shows an alert with the number of found elements.
   */
  @FXML
  private void onTraceClick() {

    try {
      // build utility trace parameters for a subnetwork trace using the prepared starting location
      UtilityTraceParameters utilityTraceParameters =
          new UtilityTraceParameters(UtilityTraceType.SUBNETWORK, Collections.singletonList(startingLocation));

      // set the defined trace configuration to the trace parameters
      utilityTraceParameters.setTraceConfiguration(utilityTraceConfiguration);

      // apply the include barriers/containers settings according to the checkboxes
      utilityTraceParameters.getTraceConfiguration().setIncludeBarriers(includeBarriersCheckBox.isSelected());
      utilityTraceParameters.getTraceConfiguration().setIncludeContainers(includeContainersCheckBox.isSelected());

      // run the utility trace and get the results
      ListenableFuture<List<UtilityTraceResult>> utilityTraceResultsFuture =
          utilityNetwork.traceAsync(utilityTraceParameters);
      utilityTraceResultsFuture.addDoneListener(() -> {
        try {
          List<UtilityTraceResult> utilityTraceResults = utilityTraceResultsFuture.get();

          if (utilityTraceResults.get(0) instanceof UtilityElementTraceResult) {
            UtilityElementTraceResult utilityElementTraceResult =
                (UtilityElementTraceResult) utilityTraceResults.get(0);

            // show an alert with the number of elements found
            int elementsFound = utilityElementTraceResult.getElements().size();
            new Alert(Alert.AlertType.INFORMATION, elementsFound + " " + "elements found.").show();

          } else {
            new Alert(Alert.AlertType.ERROR, "Trace result not a utility element.").show();
          }
        } catch (Exception e) {
          new Alert(Alert.AlertType.ERROR, "Error running utility network trace.").show();
        }
      });
    } catch (Exception e) {
      new Alert(Alert.AlertType.ERROR, "Error building trace parameters / configuration.").show();
    }
  }

  /**
   * Resets the trace configuration and UI back to the state at application start.
   */
  @FXML
  private void onResetClick() {

    // reset the utility trace configuration and traversability to the state at application start
    utilityTraceConfiguration = initialUtilityTraceConfiguration;
    utilityTraceConfiguration.getTraversability().setBarriers(initialExpression);

    // show the configuration expression from the application start in the text area
    traceConditionsTextArea.setText(generateExpressionText(initialExpression));

    // un-check the checkboxes for including barriers and containers
    includeContainersCheckBox.setSelected(false);
    includeBarriersCheckBox.setSelected(false);

    // select the first item in each ComboBox
    comparisonSourcesComboBox.getSelectionModel().select(0);
    comparisonOperatorsComboBox.getSelectionModel().select(0);
  }

  /**
   * Updates the contents of the comparison value choices ComboBox depending on the selected comparison source.
   */
  @FXML
  private void onComparisonSourceChanged() {

    // clear any previous text input
    comparisonValuesNumberField.clear();

    if (comparisonSourcesComboBox.getSelectionModel().getSelectedItem() != null) {

      // determine if we need to show a selection of values in the combo box, or a text entry field
      UtilityNetworkAttribute selectedAttribute = comparisonSourcesComboBox.getSelectionModel().getSelectedItem();

      if (selectedAttribute.getDomain() instanceof CodedValueDomain) {

        // populate and show the comparison values combo box
        List<CodedValue> comparisonValues = ((CodedValueDomain) selectedAttribute.getDomain()).getCodedValues();
        comparisonValuesChoicesComboBox.getItems().clear();
        comparisonValuesChoicesComboBox.getItems().addAll(comparisonValues);
        comparisonValuesChoicesComboBox.getSelectionModel().select(0);
        showElement(comparisonValuesChoicesComboBox);

      } else {
        switch (selectedAttribute.getDataType()){
          case BOOLEAN:
            showElement(comparisonValuesBooleanComboBox);
            break;
          case DOUBLE:
          case FLOAT:
          case INTEGER:
            showElement(comparisonValuesNumberField);
            break;
        }
      }
    }
  }

  private void showElement(Control elementToShow) {
    ArrayList<Control> elements = new ArrayList<>(
        Arrays.asList(comparisonValuesBooleanComboBox, comparisonValuesNumberField, comparisonValuesChoicesComboBox));

    elements.forEach(element -> element.setVisible(element.equals(elementToShow)));
  }

  /**
   * Converts an object representing a value into the data type specified.
   * @param value the value to convert
   * @param dataType the requested data type to which to convert
   * @return the converted value
   */
  private Object convertToDataType(Object value, UtilityNetworkAttribute.DataType dataType) {

    Object converted = null;

    switch (dataType) {
      case BOOLEAN:
        converted = Boolean.valueOf(value.toString());
        break;
      case DOUBLE:
        converted = Double.valueOf(value.toString());
        break;
      case FLOAT:
        converted = Float.valueOf(value.toString());
        break;
      case INTEGER:
        converted = Integer.parseInt(value.toString());
        break;
    }

    return converted;
  }

  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

  }
}
