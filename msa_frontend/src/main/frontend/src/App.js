import { Route, Routes } from "react-router-dom";
import "./App.css";
import CommonPage from "./pages/CommonPage";
import LoginPage from "./pages/LoginPage";

function App() {
  return (
    <Routes>
      <Route path="/" element={<CommonPage />}>
        <Route path="test" element={<LoginPage />}></Route>
      </Route>
      <Route path="/login" element={<LoginPage />}></Route>
    </Routes>
  );
}

export default App;
