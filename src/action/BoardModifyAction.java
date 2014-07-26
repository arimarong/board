package action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.*;

public class BoardModifyAction implements Action {
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("euc-kr");
		ActionForward forward = new ActionForward();
		boolean result = false;
		
		int num = Integer.parseInt(request.getParameter("BOARD_NUM"));
		
		BoardDAO boarddao = new BoardDAO();
		BoardBean boarddata = new BoardBean();
		
		boolean usercheck = boarddao.isBoardWriter(num, request.getParameter("BOARD_PASS"));
		
		if(usercheck == false){
			response.setContentType("text/html;charset=euc-kr");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('������ ������ �����ϴ�.');");
			out.println("location.href = './BoardList.bo';");
			out.println("</script>");
			out.close();
			return null;
		}
		
		try{
			boarddata.setBoard_num(num);
			boarddata.setBoard_subject("BOARD_SUBJECT");
			boarddata.setBoard_content("BOARD_CONTENT");
			result = boarddao.boardModify(boarddata);
			
			if(result == false){
				System.out.println("�Խñ� ���� ����");
				return null;
			}
			
			System.out.println("�Խñ� ���� �Ϸ�");
			
			forward.setRedirect(true);
			forward.setPath("./BoardDetailAction.bo?num = " + boarddata.getBoard_num());
			
			return forward;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
	}
}
