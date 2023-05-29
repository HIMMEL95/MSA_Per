import "./App.css";
import { Route, Routes } from "react-router-dom";
import CommonPage from "./pages/CommonPage";
import LoginPage from "./pages/LoginPage";

function App() {
  return (
    <Routes>
      <Route path="/" element={<CommonPage />}></Route>
      <Route path="/login" element={<LoginPage />}></Route>
    </Routes>
  );
}

export default App;
