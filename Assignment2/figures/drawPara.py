import matplotlib.pyplot as plt
import random
import os
from collections import defaultdict

errors = defaultdict(lambda: [])

for filename in os.listdir("E:/CPEN502/Assignment2/figures/"):
    if filename.startswith("nn_para_n"):
        with open(filename, 'r') as f:
            fl = f.readlines()
            v = float(fl[1])
            for line in fl:
                s = line.split(":")
                if len(s) == 2:
                    errors[v].append(float(s[1]))

plt.figure(1)
for a in errors.keys():
    plt.plot(errors[a], label = a, linewidth = 0.5)
plt.xlabel('Epoch')
plt.ylabel('RMS')
plt.legend()
plt.show()