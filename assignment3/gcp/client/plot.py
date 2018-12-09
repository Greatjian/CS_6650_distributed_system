import matplotlib.pyplot as plt

with open("test.csv", "r") as f:
  x, y = [], []
  for line in f:
    a, b = line.split(',')
    if (a == "time"):
      continue
    x.append(int(a))
    y.append(int(b))

plt.plot(x, y)
plt.show()
