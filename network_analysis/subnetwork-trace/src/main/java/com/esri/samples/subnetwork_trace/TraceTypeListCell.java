/* Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.esri.samples.subnetwork_trace;

import com.esri.arcgisruntime.utilitynetworks.UtilityTraceType;
import javafx.scene.control.ListCell;

/**
 * Shows the title of the UtilityTerminal in the selection list view.
 */
public class TraceTypeListCell extends ListCell<UtilityTraceType> {

  @Override
  protected void updateItem(UtilityTraceType utilityTraceType, boolean empty) {
    super.updateItem(utilityTraceType, empty);
    setText(empty || utilityTraceType == null ? null : utilityTraceType.toString());
    setGraphic(null);
  }
}