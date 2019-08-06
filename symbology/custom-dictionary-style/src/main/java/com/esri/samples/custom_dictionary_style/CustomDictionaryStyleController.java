package com.esri.samples.custom_dictionary_style;

import javafx.fxml.FXML;

import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.symbology.DictionarySymbolStyle;

public class CustomDictionaryStyleController {

  @FXML private MapView mapView;

  private DictionarySymbolStyle restaurantStyle;

  @FXML
  public void initialize() {
    try{

      // create a new map with a streets basemap and display it
      ArcGISMap map = new ArcGISMap(Basemap.createStreetsVector());
      mapView.setMap(map);

      // open the custome style file
      restaurantStyle = DictionarySymbolStyle.createFromFile("./samples-data/stylx/Restaurant.stylx");


      Portal portal = new Portal("https://www.arcgis.com/");
      PortalItem portalItem = new PortalItem(portal, "3daf83e1ec0941428526a07f2d2ae414");

      // create the restaurants layer and add it to the map
      FeatureLayer featureLayer = new FeatureLayer(portalItem, 0);

      map.getOperationalLayers().add(featureLayer);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  public void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
