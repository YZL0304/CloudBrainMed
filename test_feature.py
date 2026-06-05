import requests, json

with open(r'D:\CloudBrainMed\BrainArtifacts\datasets\CT\009_CT.nii.gz', 'rb') as f:
    r = requests.post('http://localhost:5000/inference', files={'file': f})

data = r.json()
print(f"success: {data['success']}")
print(f"positive_pixels: {data['positive_pixels']}")
print(f"feature_dim: {data.get('feature_dim', 'MISSING')}")
fv = data.get('feature_vector')
if fv:
    print(f"feature_vector: [{fv[0]:.4f}, {fv[1]:.4f}, ..., {fv[-1]:.4f}] (len={len(fv)})")
else:
    print("feature_vector: MISSING")
