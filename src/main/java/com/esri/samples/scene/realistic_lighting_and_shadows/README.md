<h1>Realistic Lighting and Shadows</h1>

<p>Show realistic lighting and shadows for the time of day.</p> 

<p><img src="RealisticLightingAndShadows.png" /></p>

<h2>How to use the sample</h2>

<p>Select one of the three lighting options to show that lighting effect on the SceneView. Select a time of day from the slider (based on 24hr clock) to show the lighting for that time of day to the SceneView.
</p>

<h2>How it works</h2>

<p>To add realistic lighting and shadows to the SceneView:</p>


<ol>
<li>Create an <code>ArcGISScene</code> and display it in a <code>SceneView</code>.</li>

<li>Create a <code>Calendar</code> to define the time of day.</li>
<li>Set the sun time to that calendar with <code>sceneView.setSunTime(calendar)</code>. </li>

<li>Set the lighting mode of the sun view to no light, light, or light and shadows with <code>sceneView.setSunLighting(LightingMode)</code>.</li>
</ol>

<h2>Relevant API</h2>

<ul>
<li><code>ArcGISScene</code></li>
<li><code>LightingMode</code></li>
<li><code>SceneView.setSunTime</code></li>
<li><code>SceneView.setSunLighting</code></li>

</ul>

<h2>Tags</h2>

<p>lighting, shadows, realistic environment, ArcGISScene</p>





