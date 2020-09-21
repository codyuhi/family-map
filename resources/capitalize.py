import sys

# print(sys.argv[1])
with open(sys.argv[1], 'r') as f:
    out = open("outputFile.txt", "w")
    for line in f:
        l=line.title()
        out.write(l)
    out.close()
