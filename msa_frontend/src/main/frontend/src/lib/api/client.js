import axios from "axios";

const client = axios.create({
  headers: {
    "Access-Token": "",
  },
});

export default client;
