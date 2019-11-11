# Trace a subnetwork

Discover all the features participating in a subnetwork with subnetwork, upstream, and downstream trace types.

![Subnetwork Trace Sample](SubnetworkTrace.png)

## Use case

This is useful for validating whether subnetworks, such as circuits or zones, are defined or edited appropriately. 

## How to use the sample

Click on one or more features while 'Add starting locations' or 'Add barriers' is selected. When a junction feature is identified, you may be prompted to select a terminal. When an edge feature is identified, the distance from the tapped location to the beginning of the edge feature will be computed. Click 'Configure' to define the type of subnetwork-based trace. To further refine the traceable area, select a 'Source Tier'. This tier's pre-defined trace configuration, which includes things such as domain network, condition barriers, and whether to include barriers (among others), will be used in this subnetwork-based trace. Click 'Trace' to highlight all features connected to the specified starting locations and not positioned beyond the barriers. Click 'Reset' to clear parameters and start over.

## How it works

1. Create a `Map` and add it to a `MapView`.
2. Using the URL to a utility network's feature service, create `FeatureLayer`s that contain the utility network's features, and add them to the operational layers of the map.
3. Create and load a `UtilityNetwork` with the same feature service URL and map.
4. Add a `GraphicsOverlay` with symbology that distinguishes starting points from barriers.
5. Add a listener for clicks on the map view, and use `mapView.identifyLayersAsync()` to identify clicked features.
6. Add a `Graphic` that represents its purpose (starting point or barrier) at the location of each identified feature.
7. Determine the type of the identified feature using `utilityNetwork.getDefinition().getNetworkSource()` passing its table name.
8. If a junction, display a terminal picker when more than one `UtilityTerminal` is found and create a `UtilityElement` with the selected terminal or the single terminal if there is only one.
9. If an edge, create a utility element from the identified feature and set its `FractionAlongEdge` using `GeometryEngine.fractionAlong()`.
10. Display a trace type picker, then create the `TraceParameters` with the selected trace type along with the collected starting locations, and barriers (if applicable).
11. Display a tier picker, then set the trace parameter's `TraceConfiguration` with the selected tier's trace configuration property.
12. Run a `utilityNetwork.traceAsync()` with the specified parameters, and get the `UtilityTraceResult`.
13. For every feature layer in this map with elements, select features by converting utility elements to `ArcGISFeature`(s) using `UtilityNetwork.fetchFeaturesForElementsAsync()`

## Relevant API

* FractionAlong
* UtilityAssetType
* UtilityDomainNetwork
* UtilityElement
* UtilityElementTraceResult
* UtilityNetwork
* UtilityNetworkDefinition
* UtilityNetworkSource
* UtilityTerminal
* UtilityTier
* UtilityTraceConfiguration
* UtilityTraceParameters
* UtilityTraceResult
* UtilityTraceType
* UtilityTraversability

## About the data

The [feature service](https://sampleserver7.arcgisonline.com/arcgis/rest/services/UtilityNetwork/NapervilleElectric/FeatureServer) in this sample represents an electric network in Naperville, Illinois, which contains a utility network used to run the subnetwork-based trace.

## Tags

condition barriers, downstream trace, network analysis, trace configuration, traversability, upstream trace, utility network, validate consistency, subnetwork trace
