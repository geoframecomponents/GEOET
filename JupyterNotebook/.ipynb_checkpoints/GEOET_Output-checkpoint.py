# -*- coding: utf-8 -*-
"""
Created on 10/29/2019

This is used to create Prospero plot

@author: Concetta D'Amato
@license: creative commons 4.0
"""

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from IPython.display import Image
import os
import plotly.graph_objects as go
import plotly.express as px
import pandas as pd
import warnings

##############################################################################################################################INTRODUZIONE####
def show_var(variabile):
    df = pd.read_csv(variabile+'_1.csv',skiprows=6, sep=',', parse_dates=[0], na_values=-9999,usecols=[1,2])
    df.columns = ['Datetime',variabile]

    fig = px.line(df, x='Datetime', y=variabile)
    if variabile=='airT':
        fig.update_yaxes(title_text='Temperature [°C]')
    if variabile=='Wind':
        fig.update_yaxes(title_text='Velocità vento [m/s] ')
    if variabile=='GHF':
        fig.update_yaxes(title_text='Flussio di calore dal suolo')
    if variabile=='Pres':
        fig.update_yaxes(title_text='Pressione')
    if variabile=='SoilMoisture_sin':
        fig.update_yaxes(title_text='Contenuto di acqua')
    if variabile=='LAI':
        fig.update_yaxes(title_text='LAI')
    if variabile=='RH':
        fig.update_yaxes(title_text='Umidità relativa')
    if variabile=='ShortwaveDirect':
        fig.update_layout(title='Short wave direct')
        fig.update_yaxes(title_text='Radiazione corta diretta [$W \cdot m^{−2}$]')
    if variabile=='ShortwaveDiffuse':
        fig.update_layout(title='Short wave diffuse')
        fig.update_yaxes(title_text='Radiazione corta diffusa [$W \cdot m^{−2}$]')
    if variabile=='LongDownwelling':
        fig.update_layout(title='Long wave')
        fig.update_yaxes(title_text='Radiazione lunga incidente [$W \cdot m^{−2}$]')
    if variabile=='Net':
        fig.update_layout(title='Net radiation')
        fig.update_yaxes(title_text='Radiazione netta [$W \cdot m^{−2}$]')
    
    #fig.update_xaxes(rangeslider_visible=True)
    fig.show()

    
##############################################################################################################################
##############################################################################################################################    PROSPERO   ####
##############################################################################################################################
def show_stress(a,b,c,d):
    warnings.filterwarnings('ignore')
    kl = pd.read_csv(a,skiprows=6,parse_dates=[1])
    kl = kl.drop(['Format'],axis=1) 
    kl.columns.values[0] = 'Date'
    kl.columns.values[1] = 'Potential'  
    kl.Potential[kl.Potential<0]=0
    #kl.Potential[kl.Potential>4]=0
    
    kl2 = pd.read_csv(b,skiprows=6,parse_dates=[1])
    kl2 = kl2.drop(['Format'],axis=1)
    kl2.columns.values[0] = 'Date'
    kl2.columns.values[1] = 'Water_Stress'
    kl2.Water_Stress[kl2.Water_Stress<0]=0
    #kl2.Water_Stress[kl2.Water_Stress>1.5]=0
      
    kl3 = pd.read_csv(c,skiprows=6,parse_dates=[1])
    kl3 = kl3.drop(['Format'],axis=1)
    kl3.columns.values[0] = 'Date'
    kl3.columns.values[1] = 'Environmental_Stress'
    #kl3.Environmental_Stress[kl3.Environmental_Stress>4]=0
    kl3.Environmental_Stress[kl3.Environmental_Stress<0]=0
        
    kl4 = pd.read_csv(d,skiprows=6,parse_dates=[1])
    kl4 = kl4.drop(['Format'],axis=1)
    kl4.columns.values[0] = 'Date'
    kl4.columns.values[1] = 'Total_Stress'
    #kl4.Total_Stress[kl4.Total_Stress>4]=0
    kl4.Total_Stress[kl4.Total_Stress<0]=0
    
    
   
    fig = px.line()
    fig.add_trace(go.Scatter(x=kl['Date'], y=kl['Potential'], mode='lines', name='Potential'))
    fig.add_trace(go.Scatter(x=kl2['Date'], y=kl2['Water_Stress'], mode='lines', name='Water_Stress'))
    fig.add_trace(go.Scatter(x=kl3['Date'], y=kl3['Environmental_Stress'], mode='lines', name='Environmental_Stress'))
    fig.add_trace(go.Scatter(x=kl4['Date'], y=kl4['Total_Stress'], mode='lines', name='Total_Stress'))
    

    fig.update_xaxes(rangeslider_visible=True)
    
    fig.update_layout(
        #title= a,
        #xaxis_title="Date",
        #yaxis_title= a,
        legend_title="Active Stress",
        font=dict(size=14))
    
    fig.show()



def show_E_T(a,b,c):
    warnings.filterwarnings('ignore')
    ############################ GRAFICO 1 ########################################
    kl = pd.read_csv(a,skiprows=6,parse_dates=[1])
    kl = kl.drop(['Format'],axis=1)
    kl.columns.values[0] = 'Date'
    kl.columns.values[1] = 'EvapoTranspiration'
    kl.EvapoTranspiration[kl.EvapoTranspiration<0]=0
   
    
    kl2 = pd.read_csv(b,skiprows=6,parse_dates=[1])
    kl2 = kl2.drop(['Format'],axis=1)
    kl2.columns.values[0] = 'Date'
    kl2.columns.values[1] = 'Evaporation'
    kl2.Evaporation[kl2.Evaporation<0]=0
  
    
    kl3 = pd.read_csv(c,skiprows=6,parse_dates=[1])
    kl3 = kl3.drop(['Format'],axis=1)
    kl3.columns.values[0] = 'Date'
    kl3.columns.values[1] = 'Transpiration'
    kl3.Transpiration[kl3.Transpiration<0]=0
    
    
    fig = px.line()
    fig.add_trace(go.Scatter(x=kl['Date'], y=kl['EvapoTranspiration'], mode='lines', name='EvapoTranspiration'))
    fig.add_trace(go.Scatter(x=kl['Date'], y=kl2['Evaporation'], mode='lines', name='Evaporation'))
    fig.add_trace(go.Scatter(x=kl['Date'], y=kl3['Transpiration'], mode='lines', name='Transpiration'))

   #fig.update_xaxes(rangeslider_visible=True)
    
    
    fig.update_layout(
        title='Compare Evaporation and Traspiration fluxes',
        #xaxis_title="Date",
        yaxis_title="[$W\cdot m^{−2}$]",
        #legend_title="Date",
        font=dict(size=14))
    fig.show()
    
def show_compare(a,b,c):
    warnings.filterwarnings('ignore')
    ############################ GRAFICO 1 ########################################
    kl = pd.read_csv(a,skiprows=6,parse_dates=[1])
    kl = kl.drop(['Format'],axis=1)
    kl.columns.values[0] = 'Date'
    kl.columns.values[1] = 'Prospero_ET'
    kl.Prospero_ET[kl.Prospero_ET<0]=0
    kl.Prospero_ET[kl.Prospero_ET>800]=0
   
    
    kl2 = pd.read_csv(b,skiprows=6,parse_dates=[1])
    kl2 = kl2.drop(['Format'],axis=1)
    kl2.columns.values[0] = 'Date'
    kl2.columns.values[1] = 'Pristley_Taylor_ET'
    kl2.Pristley_Taylor_ET[kl2.Pristley_Taylor_ET<0]=0
  
    
    kl3 = pd.read_csv(c,skiprows=6,parse_dates=[1])
    kl3 = kl3.drop(['Format'],axis=1)
    kl3.columns.values[0] = 'Date'
    kl3.columns.values[1] = 'PenmanMontheithFAO_ET'
    kl3.PenmanMontheithFAO_ET[kl3.PenmanMontheithFAO_ET<0]=0
    
    
    fig = px.line()
    fig.add_trace(go.Scatter(x=kl['Date'], y=kl2['Pristley_Taylor_ET'], mode='lines', name='Pristley_Taylor_ET'))
    fig.add_trace(go.Scatter(x=kl['Date'], y=kl['Prospero_ET'], mode='lines', name='Prospero_ET'))
    fig.add_trace(go.Scatter(x=kl['Date'], y=kl3['PenmanMontheithFAO_ET'], mode='lines', name='PenmanMontheithFAO_ET'))

   #fig.update_xaxes(rangeslider_visible=True)
    
    
    fig.update_layout(
        title='Compare EvapoTraspiration fluxes',
        #xaxis_title="Date",
        yaxis_title="[$W\cdot m^{−2}$]",
        #legend_title="Date",
        font=dict(size=14))
    fig.show()
