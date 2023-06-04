import Logo from "../../Image/honeycomb.png";
import "../../css/common/FooterContainer.css";

const FooterContainer = () => {
  return (
    <div>
      <div className="footer-container">
        <div className="footerbar">
          <div className="nav-link active" aria-current="page">
            회사소개
          </div>
          <div className="nav-link">인재채용</div>
          <div className="nav-link">회원약관</div>
          <div className="nav-link">개인정보처리방침</div>
          <div className="nav-link">제휴문의</div>
          <div className="nav-link">고객센터</div>
        </div>
        <div className="footer-content">
          <div className="footer-logo">
            <img className="footerLogo" src={Logo} alt="logo" />
            <div className="footer-title">Jobhub</div>
          </div>
          <div className="footer-info">
            <div>Jobhub 고객센터</div>
            <div>이메일 : bbluesky7738@gmail.com </div>
            <div>Jobhub, 주소 : </div>
            <div>Copyright Jobhub. All rights reserved</div>
            <div></div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FooterContainer;
