import json, sys, os
H=".devin/coverage-history.json"
cur=float(sys.argv[1])  # current global line coverage (0-100)
hist=[]
if os.path.exists(H):
    hist=json.load(open(H))
hist.append(cur); hist=hist[-5:]
json.dump(hist, open(H,"w"))
if len(hist)>=3 and (hist[-1]-hist[-3])<0.3:  # <0.3% over last 3 runs
    print("Plateau detected: <0.3% improvement across 3 runs.")
    sys.exit(2)
