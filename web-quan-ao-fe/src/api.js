import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: false, 
});

axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem("authToken");
  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

axiosInstance.interceptors.response.use(
  (res) => res,
  (err) => {
    return Promise.reject(err);
  }
);

const API = {
  get: (url, cfg) => axiosInstance.get(url, cfg).then((r) => r.data),
  post: (url, data, cfg) => axiosInstance.post(url, data, cfg).then((r) => r.data),
  put: (url, data, cfg) => axiosInstance.put(url, data, cfg).then((r) => r.data),
  delete: (url, cfg) => axiosInstance.delete(url, cfg).then((r) => r.data),
  raw: axiosInstance,
};

export default API;
