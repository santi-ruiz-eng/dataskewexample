import json
import random
from datetime import datetime
from datetime import timedelta
import numpy as np
from faker import Faker

fake = Faker('en_IE')
sites = ['mainstream.com','niche_1.com','niche_2.com','niche_3.com','niche_4.com','niche_5.com','niche_6.com','niche_7.com','niche_8.com','niche_9.com','niche_10.com']
sWeights = [0.7, 0.025, 0.025, 0.07, 0.03, 0.07, 0.03, 0.0125, 0.0125, 0.0125, 0.0125]
now = datetime.now()

dataFile = open("src/main/resources/skewedData.csv", "w")

numberOfRecords = 10000 #5000000

guestRefs = []
for i in range(numberOfRecords):
    typeOfEntity = "EVENT"
    typeOfEvent = "VIEW"
    site = np.random.choice(sites, p=sWeights)
    page = "/homepage"
    guestRef = fake.uuid4()
    sessionRef = fake.uuid4()
    current_minute = i//50000
    timestamp = fake.date_time_between_dates(now + timedelta(minutes=current_minute), now + timedelta(minutes=current_minute+1)).strftime('%Y-%m-%d %H:%M:%S')
    line = '%s,%s,%s,%s,%s,%s,%s'%(typeOfEntity,typeOfEvent,site,page,guestRef,sessionRef, timestamp)
    dataFile.write(line + "\n")

dataFile.close()