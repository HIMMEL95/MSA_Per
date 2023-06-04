import React from "react";
import { Outlet, useLocation } from "react-router-dom";
import FooterContainer from "../container/common/FooterContainer";
import HeaderContainer from "../container/common/HeaderContainer";
import MainPage from "./MainPage";

const CommonPage = () => {
  const loc = useLocation();
  console.log(loc.pathname);
  return (
    <div className="template">
      <HeaderContainer />
      {loc.pathname === "/" ? <MainPage /> : <Outlet />}
      <div className="main"></div>
      <FooterContainer />
    </div>
  );
};

export default CommonPage;
