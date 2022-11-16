import matplotlib.pyplot as plt
import random

def openLog(name, offset = 0):
    with open(name, 'r') as f_e35:
        fileList = f_e35.readlines()
        fileList = fileList[9:]
        winRate = []
        for numLine in range(len(fileList)):
            winRate.append(abs(float(fileList[numLine][-5:-1]) + offset))
        return winRate

e_0 = openLog('e_0.log')
e_2 = openLog('e_0.2.log')
e_35 = openLog('offPolicy.log')
e_5 = openLog('e_0.5.log')
on = openLog('e_3.5.log', -15)
re = openLog('reward.log')

plt.figure(1)
plt.plot(e_35, label = 'off-policy', linewidth = 0.5)
plt.plot(on, label = 'on-policy', linewidth = 0.5)
plt.xlabel('# Rounds / hundreds')
plt.ylabel('Win Rate (%)')
plt.legend()
plt.show()

plt.figure(2)
plt.plot(e_35, label = 'instant & terminal rewards', linewidth = 0.5)
plt.plot(re, label = 'only terminal reward', linewidth = 0.5)
plt.xlabel('# Rounds / hundreds')
plt.ylabel('Win Rate (%)')
plt.legend()
plt.show()

plt.figure(3)
plt.plot(e_0, label = 'e = 0', linewidth = 0.5)
plt.plot(e_35, label = 'e = 0.35', linewidth = 0.5)
plt.xlabel('# Rounds / hundreds')
plt.ylabel('Win Rate (%)')
plt.legend()
plt.show()

plt.figure(4)
plt.plot(e_2, label = 'e = 0.2', linewidth = 0.5)
plt.plot(e_35, label = 'e = 0.35', linewidth = 0.5)
plt.xlabel('# Rounds / hundreds')
plt.ylabel('Win Rate (%)')
plt.legend()
plt.show()

plt.figure(5)
plt.plot(e_5, label = 'e = 0.5', linewidth = 0.5)
plt.plot(e_35, label = 'e = 0.35', linewidth = 0.5)
plt.xlabel('# Rounds / hundreds')
plt.ylabel('Win Rate (%)')
plt.legend()
plt.show()

plt.figure(6)
plt.plot(e_35, linewidth = 0.5)
plt.xlabel('# Rounds / hundreds')
plt.ylabel('Win Rate (%)')
plt.title('Learning process when e = 0.35, alpha = 0.9, gamma = 0.9')
plt.legend()
plt.show()

