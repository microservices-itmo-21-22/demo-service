import os

class_names = ['catalog', 'booking', 'delivery']
for class_name in class_names:
    os.makedirs(f"{class_name}/api/controller", exist_ok=True)
    os.makedirs(f"{class_name}/api/model", exist_ok=True)
    os.makedirs(f"{class_name}/api/service", exist_ok=True)
    os.makedirs(f"{class_name}/impl/entity", exist_ok=True)
    os.makedirs(f"{class_name}/impl/repository", exist_ok=True)
    os.makedirs(f"{class_name}/impl/service", exist_ok=True)
    os.makedirs(f"{class_name}/impl/util", exist_ok=True)
