import client from "./client";

// Openweather
export const weather = async ({ lat, lot }) => {
  const form = new FormData();
  form.append("lat", lat);
  form.append("lot", lot);
  const rs = await client.post("/api/getWeather.do", form);
  return rs;
};
