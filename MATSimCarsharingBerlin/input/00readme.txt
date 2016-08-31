Basic Information
- contains a scenario for Berlin metropolitan region
- only for car traffic, i.e. people using, e.g. public transport are not included
- as a 1% sample, i.e. 1% of all daily car traffic is simulated
- does not include super-regional (e.g. national, international) traffic

Used Methodology
- approach is described in VSP-WP 14-15 in detail
- a simple 1% synthetic population is created with home-workplace combinations
according to commuter statistics
- CEMDAP activity-based demand generation is carried out on this synthetic population
multiple times for each agent so that a variety in activity-travel plans exists
- these plans are converted into MATSim plans and based on them and a OSM-based traffic network, a scenario is created
- MATSim transport simulation is run for this scenario in interaction with the Cadyts
calibration algorithm which helps selecting those plans from each agent's set that do
best match given traffic counts

Folder Contents
- config_be_1pct.xml  --  Scenario config file
- network.xml.gz  --  OSM-based network for Berlin
- run_160.150.plans_selected.xml.gz  --  relaxed activity-travel plans (only selected plans)


It should be possible to run the example by something like

  java -Xmx2000m -cp /path/to/matsim.jar org.matsim.run.Controler config_be_1pct.xml

For explanation see http://www.matsim.org/quickstart .
