package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDAO {
	Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	
	public BoardDAO(){
		try{
			Context init = new InitialContext();
			DataSource ds = (DataSource)init.lookup("java:comp/env/jdbc/MysqlDB");
			con = ds.getConnection();
		}catch(Exception ex){
			System.out.println("DB 연결 실패 : " + ex);
			return;
		}
	}
	
	//글의 개수 구하기
	public int getListCount(){
		int x = 0;
		
		try{
			pstmt = con.prepareStatement("SELECT COUNT(*) FROM BOARD");
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				x = rs.getInt(1);
			}
		}catch(Exception ex){
			System.out.println("getListCount 에러 : " + ex);
		}finally{
			if(rs!=null)try{rs.close();}catch(Exception ex) {}
			if(pstmt!=null)try{pstmt.close();}catch(Exception ex) {}
		}
		return x;
	}
	
	//글 목록 보기
	public List getBoardList(int page, int limit){
		String board_list_sql = "SELECT * 										"
							  + "  FROM (										"
							  + "		 SELECT ROWNUM RNUM						"
							  + "             , BOARD_NUM						"
							  + "             , BOARD_NAME						"
							  + "             , BOARD_SUBJECT					"
							  + "             , BOARD_CONTENT					"
							  + "             , BOARD_FILE						"
							  + "             , BOARD_RE_REF					"
							  + "             , BOARD_RE_LEV					"
							  + "             , BOARD_RE_SEQ					"
							  + "             , BOARD_READCOUNT					"
							  + "             , BOARD_DATE						"
							  + "          FROM (								"
							  + "                SELECT *						"
							  + "                  FROM BOARD					"
							  + "                 ORDER BY BOARD_RE_REF DESC	"
							  + "                        , BOARD_RE_SEQ ASC		"
							  + "               )								"
							  + "       )										"
							  + " WHERE RNUM >= ?								"
							  + "   AND RNUM <= ?								";
		
		List list = new ArrayList();
		
		//읽기 시작할 row 번호
		int startrow = (page - 1) * 10 + 1;
		//마지막에 읽을 row 번호
		int endrow = startrow + limit - 1;
		
		try{
			pstmt = con.prepareStatement(board_list_sql);
			pstmt.setInt(1, startrow);
			pstmt.setInt(2, endrow);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				BoardBean board = new BoardBean();
				board.setBoard_num(rs.getInt("BOARD_NUM"));
				board.setBoard_name(rs.getString("BOARD_NAME"));
				board.setBoard_subject(rs.getString("BOARD_SUBJECT"));
				board.setBoard_content(rs.getString("BOARD_CONTENT"));
				board.setBoard_file(rs.getString("BOARD_FILE"));
				board.setBoard_re_ref(rs.getInt("BOARD_RE_REF"));
				board.setBoard_re_lev(rs.getInt("BOARD_RE_LEV"));
				board.setBoard_re_seq(rs.getInt("BOARD_RE_SEQ"));
				board.setBoard_readcount(rs.getInt("BOARD_READCOUNT"));
				board.setBoard_date(rs.getDate("BOARD_DATE"));
				list.add(board);
			}
			
			return list;
		}catch(Exception ex){
			System.out.println("getBoardList 에러 : " + ex);
		}finally{
			if(rs!=null)try{rs.close();}catch(Exception ex) {}
			if(pstmt!=null)try{pstmt.close();}catch(Exception ex) {}
		}
		
		return null;
	}
	
	//글 내용 보기
	public BoardBean getDetail(int num) throws Exception{
		BoardBean board = null;
		
		try{
			pstmt = con.prepareStatement("SELECT * FROM BOARD WHERE BOARD_NUM = ?");
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				board = new BoardBean();
				
				board.setBoard_num(rs.getInt("BOARD_NUM"));
				board.setBoard_name(rs.getString("BOARD_NAME"));
				board.setBoard_subject(rs.getString("BOARD_SUBJECT"));
				board.setBoard_content(rs.getString("BOARD_CONTENT"));
				board.setBoard_file(rs.getString("BOARD_FILE"));
				board.setBoard_re_ref(rs.getInt("BOARD_RE_REF"));
				board.setBoard_re_lev(rs.getInt("BOARD_RE_LEV"));
				board.setBoard_re_seq(rs.getInt("BOARD_RE_SEQ"));
				board.setBoard_readcount(rs.getInt("BOARD_READCOUNT"));
				board.setBoard_date(rs.getDate("BOARD_DATE"));
			}
			
			return board;
		}catch(Exception ex){
			System.out.println("getDetail 에러 : " + ex);
		}finally{
			if(rs!=null)try{rs.close();}catch(Exception ex) {}
			if(pstmt!=null)try{pstmt.close();}catch(Exception ex) {}
		}
		
		return null;
	}
	
	//글 등록
	public boolean boardInsert(BoardBean board){
		int num = 0;
		int result = 0;
		String sql = "";
		
		try{
			pstmt=con.prepareStatement("SELECT MAX(BOARD_NUM) FROM BOARD");
			rs = pstmt.executeQuery();
			
			if(rs.next())
				num = rs.getInt(1) + 1;
			else
				num = 1;
			
			sql = "INSERT					"
				+ "  INTO BOARD				"
				+ "     (					"
				+ "       BOARD_NUM			"
				+ "     , BOARD_NAME		"
				+ "     , BOARD_PASS		"
				+ "     , BOARD_SUBJECT		"
				+ "     , BOARD_CONTENT		"
				+ "     , BOARD_FILE		"
				+ "     , BOARD_RE_REF		"
				+ "     , BOARD_RE_LEV		"
				+ "     , BOARD_RE_SEQ		"
				+ "     , BOARD_RE.REDCOUNT	"
				+ "     , BOARD_DATE		"
				+ "     )					"
				+ "VALUES					"
				+ "     {					"
				+ "       ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , SYSDATE			";

			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setString(2, board.getBoard_name());
			pstmt.setString(3, board.getBoard_pass());
			pstmt.setString(4, board.getBoard_subject());
			pstmt.setString(5, board.getBoard_content());
			pstmt.setString(6, board.getBoard_file());
			pstmt.setInt(7, num);
			pstmt.setInt(8, 0);
			pstmt.setInt(9, 0);
			pstmt.setInt(10, 0);
			
			result = pstmt.executeUpdate();
			if(result==0) return false;
			
			return true;
		}catch(Exception ex){
			System.out.println("boardInsert 에러 : " + ex);
		}finally{
			if(rs!=null)try{rs.close();}catch(Exception ex) {}
			if(pstmt!=null)try{pstmt.close();}catch(Exception ex) {}
		}
		
		return false;
	}
	
	//글 답변
	public int boardRely(BoardBean board){
		String board_max_sql = "SELECT MAX(BOARD_NUM) FROM BOARD";
		String sql = "";
		int num = 0;
		int result = 0;
		
		int re_ref = board.getBoard_re_ref();
		int re_lev = board.getBoard_re_lev();
		int re_seq = board.getBoard_re_seq();
		
		try{
			pstmt = con.prepareStatement(board_max_sql);
			rs = pstmt.executeQuery();
			if(rs.next()) num = rs.getInt(1) + 1;
			else num = 1;
			
			sql = "UPDATE BOARD"
				+ "   SET BOARD_RE_SEQ = BOARD_RE_SEQ + 1"
				+ " WHERE BOARD_RE_REF = ?"
				+ "   AND BOARD_RE_SEQ = ?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, re_ref);
			pstmt.setInt(2, re_seq);
			
			result = pstmt.executeUpdate();
			re_seq = re_seq + 1;
			re_lev = re_lev + 1;
			
			sql = "INSERT					"
				+ "  INTO BOARD				"
				+ "     (					"
				+ "       BOARD_NUM			"
				+ "     , BOARD_NAME		"
				+ "     , BOARD_PASS		"
				+ "     , BOARD_SUBJECT		"
				+ "     , BOARD_CONTENT		"
				+ "     , BOARD_FILE		"
				+ "     , BOARD_RE_REF		"
				+ "     , BOARD_RE_LEV		"
				+ "     , BOARD_RE_SEQ		"
				+ "     , BOARD_READCOUNT	"
				+ "     , BOARD_DATE		"
				+ "     )					"
				+ "VALUES					"
				+ "     {					"
				+ "       ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , ?					"
				+ "     , SYSDATE			"
				+ "     )					";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setString(2, board.getBoard_name());
			pstmt.setString(3, board.getBoard_pass());
			pstmt.setString(4, board.getBoard_subject());
			pstmt.setString(5, board.getBoard_content());
			pstmt.setString(6, "");	// 답장에는 파일을 업로드하지 않음
			pstmt.setInt(7, num);
			pstmt.setInt(8, 0);
			pstmt.setInt(9, 0);
			pstmt.setInt(10, 0);
			pstmt.executeUpdate();
			
			return num;
		}catch(SQLException ex){
			System.out.println("boardReply 에러 : " + ex);
		}finally{
			if(rs!=null)try{rs.close();}catch(Exception ex) {}
			if(pstmt!=null)try{pstmt.close();}catch(Exception ex) {}
		}
		
		return 0;
	}
	
	//글 수정
	public boolean boardModify(BoardBean modifyboard) throws Exception{
		String sql = "UPDATE BOARD"
				   + "   SET BOARD_SUBJECT = ?"
				   + "     , BOARD_CONTENT = ?"
				   + " WHERE BOARD_NUM = ?";
		try{
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, modifyboard.getBoard_subject());
			pstmt.setString(2, modifyboard.getBoard_content());
			pstmt.setInt(3, modifyboard.getBoard_num());
			pstmt.executeUpdate();
			return true;
		}catch(Exception ex){
			System.out.println("boardmodify 에러 : " + ex);
		}finally{
			if(rs!=null)try{rs.close();}catch(Exception ex) {}
			if(pstmt!=null)try{pstmt.close();}catch(Exception ex) {}
		}
		return false;
	}
	
	//글 삭제
	public boolean boardDelete(int num){
		String board_delete_sql = "DELETE FROM BOARD WHERE BOARD_NUM = ?";
		int result = 0;
		
		try{
			pstmt = con.prepareStatement(board_delete_sql);
			pstmt.setInt(1, num);
			result = pstmt.executeUpdate();
			if(result == 0) return false;
			
			return true;
		}catch(Exception ex){
			System.out.println("boardDelete 에러 : " + ex);
		}finally{
			try{
				if(pstmt != null)pstmt.close();
			}catch(Exception ex){}
		}
		
		return false;
	}
	
	//조회수 업데이트
	public void setReadCountUpdate(int num) throws Exception{
		String sql = "UPDATE BOARD"
				   + "   SET BOARD_READCOUNT = BOARD_READCOUNT + 1"
				   + " WHERE BOARD_NUM = " + num;
		
		try{
			pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();
		}catch(Exception ex){
			System.out.println("setReadCountUpdate 에러 : " + ex);
		}
	}
	
	//글쓴이인지 확인
	public boolean isBoardWriter(int num, String pass){
		String board_sql = "SELECT * FROM WHERE BOARD_NUM = ?";
		
		try{
			pstmt = con.prepareStatement(board_sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			rs.next();
			
			if(pass.equals(rs.getString("BOARD_PASS"))) return true;
		}catch(Exception ex){
			System.out.println("isBoardWriter 에러 : " + ex);
		}
		
		return false;
	}
}
