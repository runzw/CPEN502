import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import norm
import statistics
  
x_axis = []
with open('RLRobot.txt', 'r') as f:
    lines = f.readlines()
    for line in lines:
        s = line.split(",")
        if len(s) > 2:
            print(float(s[5]))
            x_axis.append(float(s[5]))
  
fig = plt.figure(figsize =(10, 7))
 
# Creating plot
plt.boxplot(x_axis)
 
# show plot
plt.show()