package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.BoardBean;
import db.BoardDAO;

public class BoardReplyAction implements Action {
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("euc-kr");
		ActionForward forward = new ActionForward();
		
		BoardDAO boarddao = new BoardDAO();
		BoardBean boarddata = new BoardBean();
		int result = 0;
		
		boarddata.setBoard_num(Integer.parseInt(request.getParameter("BOARD_NUM")));
		boarddata.setBoard_name(request.getParameter("BOARD_NAME"));
		boarddata.setBoard_pass(request.getParameter("BOARD_PASS"));
		boarddata.setBoard_subject(request.getParameter("BOARD_SUBJECT"));
		boarddata.setBoard_content(request.getParameter("BOARD_CONTENT"));
		boarddata.setBoard_re_ref(Integer.parseInt(request.getParameter("BOARD_RE_REF")));
		boarddata.setBoard_re_seq(Integer.parseInt(request.getParameter("BOARD_RE_SEQ")));
		
		result = boarddao.boardRely(boarddata);
		if(result == 0){
			System.out.println("담장 등록 실패");
			return null;
		}
		
		System.out.println("답장 등록 완료");
		
		forward.setRedirect(true);
		forward.setPath("./BoardDetailAction.bo?num = " + result);
		return forward;
	}
}
