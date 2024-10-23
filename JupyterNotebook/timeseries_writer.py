# -*- coding: utf-8 -*-
"""
Created on 10/29/2019

This is used to create timeseries input for OMS simulations

@author: Niccolo` Tubini
@license: creative commons 4.0
"""
import pandas as pd
import numpy as np


def write_timeseries_csv(dataframe, file_name):
	'''
	Save a timeseries dataframe to .csv file with OMS format
	
	:param dataframe: dataframe containing the timeseries.
	:type dataframe: pandas.dataframe
	
	:param file_name: name of the output path.
	:type file_name: str

	'''
	number_column = dataframe.shape[1]-2
	value = []
	ID = []
	double = []
	commas = []
	for i in range(0,number_column):
		value.append(',value_'+str(i+1))
		ID.append(','+str(i+1))
		double.append(',double')
		commas.append(',')
    
	line_4 = '@H,timestamp'+' '.join(value) + '\n'
	line_5 = 'ID,'+' '.join(ID) + '\n'
	line_6 = 'Type,Date' + ''.join(double) + '\n'
	line_7 = 'Format,yyyy-MM-dd HH:mm' + ' '.join(commas) + '\n'

	with open(file_name,'w') as file:
		file.write('@T,table\nCreated,2019-11-28 18:35\nAuthor,HortonMachine library\n')
		file.write(line_4)
		file.write(line_5)
		file.write(line_6)
		file.write(line_7)
    
	dataframe.to_csv(file_name, header=False, index=False, mode="a", date_format='%Y-%m-%d %H:%M')
	print ('\n\n***SUCCESS writing!  '+ file_name)
	return