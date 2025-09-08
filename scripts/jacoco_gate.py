import sys, os
import xml.etree.ElementTree as ET

p = sys.argv[1] if len(sys.argv)>1 else "target/site/jacoco/jacoco.xml"
enforce = os.environ.get("ENFORCE","false").lower() == "true"
tree = ET.parse(p)
counters = {c.get("type"): c for c in tree.findall(".//counter")}
def pct(kind):
    c = counters[kind]; miss = int(c.get("missed")); cov = int(c.get("covered"))
    return 100.0 * cov / max(1, miss + cov)
lines = pct("LINE"); branches = pct("BRANCH")
print(f"JaCoCo coverage: lines={lines:.2f}% branches={branches:.2f}%")
if enforce and (lines < 95.0 or branches < 90.0):
    sys.exit(1)
