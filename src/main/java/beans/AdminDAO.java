package beans;

import java.sql.*;
import java.util.*;

public class AdminDAO {

	//1) ��������� ������ Ŭ������ ��ü ����
	private DBConnectionMgr pool=null;
		
	//2. �������� ������ ��� �ʿ�� �ϴ� ������� ����
	private Connection con=null;
	private PreparedStatement pstmt=null;
	private ResultSet rs=null;
	private String sql="";
		
	//2) �����ڸ� ���ؼ� �ڵ����� ��ü�� ���� �� �ֵ��� ����
	public AdminDAO() {
		try {
			pool=DBConnectionMgr.getInstance();
			System.out.println("pool=>"+pool);
		}catch (Exception e) {
			System.out.println("DB���� ����=>"+e);
		}
	}
	
	
	//�޼���
	
	
	//1) �����ڷα���
	//select id, passwd from member where id='nup' and passwd='1234';
	public boolean adminLoginCheck(String id, String passwd) {
		//1. DB����
		boolean check=false;
		//2. SQL ����->���
		try {
			con=pool.getConnection();
			System.out.println("con=>"+con);
			sql="select admin_id, admin_passwd from admin where admin_id=? and admin_passwd=?";
			//pstmt��ü ����->NullPointerException �߻���
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, passwd);
			rs=pstmt.executeQuery();
			check=rs.next(); //������O->true, x->false
		}catch (Exception e) {
			System.out.println("adminLoginCheck() �����߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return check;
	}	
	
	
	//5-3) �����ڴг��� ã�� ���� �޼���
	public AdminDTO getAdminNickname(String mem_id) {
		AdminDTO nick=null; //id���� �ش�Ǵ� ���ڵ� 1���� ����
		try {
			con=pool.getConnection();
			sql="select admin_nickname from admin where admin_id=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, mem_id); //å�� ���, sql���� Ȯ�κҰ�
			rs=pstmt.executeQuery();
			//ã�� ���ڵ尡 1��->if(rs.next())
			if (rs.next()) {
				//ã�� ��->Setter Method�� �Ű������� ����=>���� ���=>Getter Method
				nick=new AdminDTO();
				nick.setAdmin_nickname(rs.getString("admin_nickname"));
			}
			System.out.println("admin_nickname=>"+nick.getAdmin_nickname());
		}catch (Exception e) {
			System.out.println("getAdminNickname �����߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return nick;
	}	
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
}
