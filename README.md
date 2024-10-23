# GEOET
GEOET (GEOframe EvapoTranspiration) is a Java-based suite of evapotranspiration (ET) models that form part of the GEOSPACE system within the GEOframe framework. GEOET is designed to simulate ET processes within the soil-plant-atmosphere continuum by leveraging multiple models of varying complexity. These models allow for a detailed analysis of how water and energy are exchanged between the Earth’s surface and the atmosphere.
GEOET is based on its precursor, GEOframe-ETP model (Bottazzi, 2020; Bottazzi et al., 2021) whose original source code is available at https://github.com/geoframecomponents/ETP. 

# Scientific Overview
GEOET incorporates several approaches to simulate evapotranspiration:

- Priestley-Taylor Model (Priestley and Taylor, 1972): A widely used empirical model that relates evaporation to the available energy, providing a simple yet effective approach to potential ET.
- Penman-Monteith FAO Model (Allen et al., 1998): A physically-based model that accounts for energy balance and aerodynamic factors to estimate ET0 (reference evapotranspiration), often used in agriculture.
- GEOframe-Prospero Model (Bottazzi, 2020; Bottazzi et al., 2021): A more detailed physically-based model that splits ET into soil evaporation (Es) and plant transpiration (El), and incorporates plant hydraulics and radiation partitioning.

GEOET is designed to accommodate multiple stress models to adjust for environmental factors that affect ET, such as:

- Jarvis Stomatal Conductance Model (Jarvis et al., 1976): Adjusts ET based on the availability of radiation, temperature, humidity, and soil moisture.
- Medlyn Stomatal Conductance Model (Medlyn et al., 2011): A more mechanistic approach that incorporates plant physiological responses to environmental conditions.
The flexibility of GEOET allows researchers to choose from different ET models depending on their requirements, enabling the study of both simple and complex hydrological processes.

This release allows the users to compute:
- potential and actual soil evaporation using the Penman Monteith FAO model
- potential and actual evapotraspiration according two different methods:
  - Priestley Taylor model
  - Penman Montheith FAO model
- potential and actual transpiration according Schimansky and Or method, implemented in the Prospero model 
- potential and actual evapotranspiration combining Prospero model for the transpiration and Penman Montheith FAO model for the soil evaporation.

# Key Features
- Support for multiple ET formulations, from simple empirical models to complex physically-based ones.
- Inclusion of water stress factors and plant hydraulic models.
- Integration with other GEOframe components (e.g., WHETGEO) for a complete ecohydrological simulation.
- Designed to handle detailed energy and water flux partitioning between soil evaporation and plant transpiration.
The modularity of the GEOET model allows easy comparison between different ET approaches and their parameterizations within a unified framework.

# Executables
[GEOET Executables](https://github.com/GEOframeOMSProjects/OMS_Project_GEOET)

# Contributing
We welcome contributions! Please follow the GEOframe community guidelines.

# Acknowledgements
GEOframe-ETP model is the precursor of GEOET.
Concetta D’Amato did the refactoring of the code with a new structure of the java classes and methods.
Concetta D’Amato and Riccardo Rigon developed the new theoretical parts (Authors).
GEOET is part of the broader GEOframe project for ecohydrological and hydrological modeling.
