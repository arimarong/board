package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import db.BoardBean;
import db.BoardDAO;

public class BoardAddAction implements Action {
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception{
		BoardDAO boarddao = new BoardDAO();
		BoardBean boarddata = new BoardBean();
		ActionForward forward = new ActionForward();
		
		String realFolder = "";
		String saveFolder = "boardupload";
		
		int fileSize = 5*1024*1024;
		
		realFolder = request.getSession().getServletContext().getRealPath(saveFolder);
		
		boolean result = false;
		
		try{
			MultipartRequest multi = null;
			
			multi = new MultipartRequest(request, realFolder, fileSize, "euc-kr", new DefaultFileRenamePolicy());
			
			boarddata.setBoard_name(multi.getParameter("BOARD_NAME"));
			
			boarddata.setBoard_pass(multi.getParameter("BOARD_PASS"));
			boarddata.setBoard_subject(multi.getParameter("BOARD_SUBJECT"));
			boarddata.setBoard_content(multi.getParameter("BOARD_CONTENT"));
			boarddata.setBoard_file(multi.getFilesystemName((String)multi.getFileNames().nextElement()));
			
			result = boarddao.boardInsert(boarddata);
			
			if(result == false){
				System.out.println("게시판 등록 실패");
				return null;
			}
			
			System.out.println("게시판 등록 완료");
			
			forward.setRedirect(true);
			forward.setPath("./BoardList.bo");
			
			return forward;
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
}
