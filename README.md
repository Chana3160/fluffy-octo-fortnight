Weather Data Service
Project Overview
This project involves connecting to the Visual Crossing Weather API using Java to fetch weather data for cities worldwide. The fetched data is stored for future analysis, and various temperature-related calculations are performed on the data. The system also includes Dockerization for easy deployment.

What Was Done:
Integration with Visual Crossing Weather API

The project connects to the Visual Crossing Weather API to fetch weather data.
The connection is made using Java's HTTP capabilities, and the data is retrieved for a range of cities worldwide.
The data fetched includes temperature data (min, max) and timestamped hourly weather information.
Post-Construction of Weather Data

After retrieving the data from the Visual Crossing Weather API, the data is post-processed in the application.
The fetched data includes temperature readings and timestamps that are then parsed and stored in the database for further processing.
Data is fetched for both historical and real-time weather information.
Database Integration (In-Memory Database)

The project uses an in-memory H2 database for storage during development and testing. The data is stored temporarily for the duration of the application’s runtime.
Database Connection Configuration:
The application is configured to connect to the H2 in-memory database.
Since it's an in-memory database, the data will not persist after the application stops, making it ideal for development purposes.
Features Implemented

Cold Wave Detection:
A feature to detect "cold waves" based on average temperatures over a period of time.

Temperature Calculations:

Min/Max Temperatures per City:
Queries that calculate the minimum and maximum recorded temperatures for each city.

Average Temperature Calculation:
An additional query to compute average temperatures for each city, considering both daily and historical temperature data.

Deployment

The service is Dockerized using a Dockerfile and deployed to Azure Kubernetes Service (AKS).
A Kubernetes deployment file and service file were created to manage the application within the AKS cluster.


## Database Queries (Spring Data JPA)

### 1. **Detecting Cold Waves**
```sql
WITH daily_avg_temp AS (
    SELECT w.city, w.timestamp::date AS day_date,
           AVG((wh.temp - 32) * 5 / 9) AS avg_temp
    FROM weather_day w
    JOIN weather_hour wh ON w.id = wh.weather_day_id
    GROUP BY w.city, w.timestamp::date
), 
coldwave_groups AS (
    SELECT city, day_date, avg_temp,
           SUM(CASE WHEN avg_temp < 10 THEN 1 ELSE 0 END)
           OVER (PARTITION BY city ORDER BY day_date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS coldwave_group,
           ROW_NUMBER() OVER (PARTITION BY city ORDER BY day_date) -
           ROW_NUMBER() OVER (PARTITION BY city, CASE WHEN avg_temp < 10 THEN 1 ELSE 0 END ORDER BY day_date) AS group_id
    FROM daily_avg_temp
) 
SELECT city, group_id, MIN(day_date) AS start_date, MAX(day_date) AS end_date,
       COUNT(*) AS coldwave_length 
FROM coldwave_groups 
WHERE avg_temp < 10 
GROUP BY city, group_id 
ORDER BY city, start_date;
```
**Explanation:** This query calculates average daily temperature per city, then identifies continuous periods where temperatures remain below 10°C, classifying them as cold waves.

### 2. **Finding Minimum Temperature Per City**
```sql
SELECT city, MIN(min_temp) AS min_temperature
FROM weather_day
GROUP BY city;
```
**Explanation:** This query retrieves the lowest recorded temperature for each city.

### 3. **Finding Maximum Temperature Per City**
```sql
SELECT city, MAX(max_temp) AS max_temperature
FROM weather_day
GROUP BY city;
```
**Explanation:** This query retrieves the highest recorded temperature for each city.

### 4. **Finding Average Temperature in a Date Range**
```sql
SELECT city, AVG(min_temp) AS avg_min_temp, AVG(max_temp) AS avg_max_temp
FROM weather_day
WHERE timestamp BETWEEN :startDate AND :endDate
GROUP BY city
ORDER BY city;
```
**Explanation:** This query calculates the average min and max temperatures per city within a given date range.

---

## Dockerization (Dockerfile)

Create a `Dockerfile` to package the Spring Boot application:
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/weather-service.jar weather-service.jar
ENTRYPOINT ["java", "-jar", "weather-service.jar"]
EXPOSE 8080
```

### Build and Push Docker Image
```sh
docker build -t weather-service:latest .
docker tag weather-service:latest myweatherregistry.azurecr.io/weather-service:latest
docker push myweatherregistry.azurecr.io/weather-service:latest
```

---

## Deploying to Azure Kubernetes Service (AKS)

### 1. **Login to Azure**
```sh
az login
```

### 2. **Create Azure Resources**
```sh
az group create --name weatherResourceGroup --location eastus
az acr create --resource-group weatherResourceGroup --name myweatherregistry --sku Basic
az aks create --resource-group weatherResourceGroup --name weatherAKSCluster --node-count 1 --enable-managed-identity --generate-ssh-keys
az aks get-credentials --resource-group weatherResourceGroup --name weatherAKSCluster
```

### 3. **Create Deployment YAML (deployment.yaml)**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: weather-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: weather-service
  template:
    metadata:
      labels:
        app: weather-service
    spec:
      containers:
      - name: weather-service
        image: myweatherregistry.azurecr.io/weather-service:latest
        ports:
        - containerPort: 8080
```

### 4. **Create Service YAML (service.yaml)**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: weather-service
spec:
  selector:
    app: weather-service
  ports:
    - protocol: TCP
      port: 85
      targetPort: 8085
  type: LoadBalancer
```

### 5. **Deploy the Service**
```sh
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl get service weather-service
```

---

## Conclusion
This project demonstrates:
- Fetching weather data from the Visual Crossing Weather API.
- Storing and analyzing weather data using a database.
- Querying and processing data with native SQL.
- Deploying a Java Spring Boot application using Docker and Kubernetes on Azure.

## Notes:
- Make sure the `weather-service.jar` file is generated in the `target` directory before building the Docker image.
- Replace `myweatherregistry.azurecr.io` with your actual Azure Container Registry URL.

