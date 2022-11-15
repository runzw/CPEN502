import matplotlib.pyplot as plt

with open('onPolicy.log', 'r') as f_e35:
    fileList = f_e35.readlines()
    fileList = fileList[8:]
    winRateE35 = []
    for numLine in range(len(fileList)):
        winRateE35.append(float(fileList[numLine][-5:-1]))

plt.figure(1)
plt.plot(winRateE35, 'b', label = 'e = 0.35', linewidth = 0.5)
plt.xlabel('Number of Rounds (hundreds)')
plt.ylabel('Winning Rate (%)')
plt.legend()

plt.show()