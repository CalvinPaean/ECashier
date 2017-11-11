import sys
path = sys.stdin.read().strip()
i = 0
with open(path, 'rb') as f:
    while 1:
        c = f.read(1)
        if not c: break
        if i != 0 and i % 24 == 0: print ""
        sys.stdout.write("%-5s" % hex(ord(c)))
	i += 1

print ""