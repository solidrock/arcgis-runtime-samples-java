# Offline Routing

Solve a route on-the-fly using only offline data.

![](OfflineRouting.gif)

## How to use the sample

Left-click near a road to add a stop to the route. A number graphic will show its order in the route. After adding at least 2 stops, a route will display. Use the combo box in the top left corner to choose between the travel modes "Fastest" and "Shortest" (how the route is optimized). To move a stop, right-click the graphic to select it, then move your mouse to reposition, and finally right-click again to set the new position. The route will update on-the-fly while moving stops. The green box marks the boundary of the route geodatabase.

## How it works

To display a `Route` using a `RouteTask` with offline data:

1. Create the map's `Basemap` from a local tile package using a `TileCache` and `ArcGISTiledLayer`
2. Create a `RouteTask` with an offline locator geodatabase
3. Get the `RouteParameters` using `routeTask.createDefaultParameters()`
4. Create `Stop`s and add them to the route task's parameters.
5. Solve the `Route` using `routeTask.solveRouteAsync(routeParameters)`
6. Create a graphic with the route's geometry and a `SimpleLineSymbol` and display it on another `GraphicsOverlay`.

## Relevant API

* ArcGISMap
* ArcGISTiledLayer
* Graphic
* GraphicsOverlay
* MapView
* Route
* RouteTask
* RouteParameters
* RouteResult
* SimpleLineSymbol
* Stop
* TextSymbol
* TileCache
