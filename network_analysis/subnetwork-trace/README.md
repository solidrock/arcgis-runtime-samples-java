# Trace a subnetwork

Discover all the features participating in a subnetwork with subnetwork, upstream, and downstream trace types.

![](SubnetworkTrace.JPG)

## Use case

This is useful for validating whether subnetworks, such as circuits or zones, are defined or edited appropriately. 

## How to use the sample

Tap on one or more features while 'Add starting locations' or 'Add barriers' is selected. When a junction feature is identified, you may be prompted to select a terminal. When an edge feature is identified, the distance from the tapped location to the beginning of the edge feature will be computed. Click 'Configure' to define the type of subnetwork-based trace. To further refine the traceable area, select a 'Source Tier'. This tier's pre-defined trace configuration, which includes things such as domain network, condition barriers, and whether to include barriers (among others), will be used in this subnetwork-based trace.

## How it works

1.  Create a `MapView` and subscribe to its `GeoViewTapped` event.
2.  Create and load a `Map` that contains `FeatureLayer`(s) that are part of a utility network.
3.  Create and load a `UtilityNetwork` using the utility network feature service URL and the map created in step 2.
4.  Add a `GraphicsOverlay` with symbology that distinguishes starting locations from barriers.
5.  Identify features on the map and add a `Graphic` that represents its purpose (starting location or barrier) at the tapped location.
6.  Create a `UtilityElement` for the identified feature.
7.  Determine the type of this element using its `NetworkSource.SourceType` property.
8.  If the element is a junction with more than one terminal, display a terminal picker. Then set the junction's `Terminal` property with the selected terminal.
9.  If an edge, set its `FractionAlongLine` property using `GeometryEngine.FractionAlong`.
10. Add this `UtilityElement` to a collection of starting locations or barriers.
11. Display a trace type picker, then create the `TraceParameters` with the selected trace type along with the collected starting locations. 
12. If any barriers were collected, add them to the `TraceParameters.Barriers`.
13. Display a tier picker, then set the `TraceParameters.TraceConfiguration` with the selected tier's `TraceConfiguration` property.
14. Run a `UtilityNetwork.TraceAsync` with the specified parameters.
15. For every `FeatureLayer` in this map, select the features using the `UtilityElement.ObjectId` from the filtered list of `UtilityElementTraceResult.Elements`.

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
