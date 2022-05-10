package beans;

import java.sql.*;
import java.util.*;

public class MemberDAO {

	//��������� ������ Ŭ������ ��ü ����
	private DBConnectionMgr pool=null;
	
	//�������� ������ ��� �ʿ�� �ϴ� ������� ����
	private Connection con=null;
	private PreparedStatement pstmt=null;
	private ResultSet rs=null;
	private String sql="";
	
	//�����ڸ� ���ؼ� �ڵ����� ��ü�� ���� �� �ֵ��� ����
	public MemberDAO() {
		try {
			pool=DBConnectionMgr.getInstance();
			System.out.println("pool=>"+pool);
		}catch (Exception e) {
			System.out.println("DB���� ����=>"+e);
		}
	}
	
	//�޼��� �ۼ�
	
	//1) ȸ���α���
	public boolean loginCheck(String id, String passwd) {
		boolean check=false;
		try {
			con=pool.getConnection();
			System.out.println("con=>"+con);
			sql="select mem_id, log_passwd from login where mem_id=? and log_passwd=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, passwd);
			rs=pstmt.executeQuery();
			check=rs.next();
		}catch (Exception e) {
			System.out.println("loginCheck() �����߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return check;
	}

	// 1-2) �г���, ����Ʈ, ��� ����ó�� ���� �޼���
	public MemberDTO getNPG(String mem_id) {
		MemberDTO npg = null;
		try {
			con = pool.getConnection();
			sql = "select log_nickname, log_point, log_grade from login where mem_id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, mem_id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				npg = new MemberDTO();
				npg.setMem_nickname(rs.getString("log_nickname"));
				npg.setMem_point(rs.getInt("log_point"));
				npg.setMem_grade(rs.getInt("log_grade"));
			}
			System.out.println("log_nickname=>" + npg.getMem_nickname());
			System.out.println("log_point=>" + npg.getMem_point());
			System.out.println("log_grade=>" + npg.getMem_grade());
		} catch (Exception e) {
			System.out.println("getNPG �����߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return npg;
	}

	
	//2) id �ߺ�üũ
	public boolean checkId(String id) {
		boolean check=false;
		try {
			con=pool.getConnection();
			sql="select mem_id from member where mem_id=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, id);
			rs=pstmt.executeQuery();
			check=rs.next();
		}catch (Exception e) {
			System.out.println("checkId() �����߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return check;
	}
	
	//2-1) �г��� �ߺ�üũ
	public boolean checkNickname(String nickname) {
		boolean check=false;
		
		try {
			con=pool.getConnection();
			sql="select mem_nickname from member where mem_nickname=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, nickname);
			rs=pstmt.executeQuery();
			check=rs.next();
		}catch (Exception e) {
			System.out.println("checkNickname() �����߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return check;
	}
	
	
	//3) �����ȣ
	
	
	//4) ȸ������
	public boolean memberInsert(MemberDTO mem) {
		boolean check=false;
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			sql="insert into member values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, mem.getMem_id());
			pstmt.setString(2, mem.getMem_passwd());
			pstmt.setString(3, mem.getMem_name());
			pstmt.setString(4, mem.getMem_nickname());
			pstmt.setString(5, mem.getMem_birth());
			pstmt.setString(6, mem.getMem_tel());
			pstmt.setString(7, mem.getMem_email());
			pstmt.setString(8, mem.getMem_addr());
			pstmt.setString(9, mem.getMem_genre());
			pstmt.setInt(10, mem.getMem_point());
			pstmt.setInt(11, mem.getMem_grade());
			
			int insert1=pstmt.executeUpdate();
			con.commit();
			System.out.println("insert1(��� ���̺� ������ �Է�����)=>"+insert1);
			if (insert1 > 0) {
				check=true;
			}
		}catch (Exception e) {
			System.out.println("memberInsert() �����߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt);
		}
		
		//�α��� ���̺� insert
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			sql="insert into login values(?, ?, ?, ?, ?)";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, mem.getMem_id());
			pstmt.setString(2, mem.getMem_passwd());
			pstmt.setString(3, mem.getMem_nickname());
			pstmt.setInt(4, mem.getMem_point());
			pstmt.setInt(5, mem.getMem_grade());
			
			int insert2=pstmt.executeUpdate();
			con.commit();
			System.out.println("insert2(�α��� ���̺� ������ �Է�����)=>"+insert2);
			if (insert2 > 0) {
				check=true;
			}
		}catch (Exception e) {
			System.out.println("loginInsert() �����߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt);
		}
		
		return check;
	}
	
	//5) ȸ������->Ư�� ȸ�� ã��
	public MemberDTO getMember(String mem_id) {
		MemberDTO mem=null; //id���� �ش�Ǵ� ���ڵ� 1���� ����
		try {
			con=pool.getConnection();
			sql="select * from member where mem_id=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, mem_id); //å�� ���, sql���� Ȯ�κҰ�
			rs=pstmt.executeQuery();
			//ã�� ���ڵ尡 1��->if(rs.next())
			if (rs.next()) {
				//ã�� ��->Setter Method�� �Ű������� ����=>���� ���=>Getter Method
				mem=new MemberDTO();
				mem.setMem_id(rs.getString("mem_id")); //<%=mem.getMem_id()%>
				mem.setMem_passwd(rs.getString("mem_passwd"));
				mem.setMem_name(rs.getString("mem_name"));
				mem.setMem_nickname(rs.getString("mem_nickname"));
				mem.setMem_birth(rs.getString("mem_birth"));
				mem.setMem_tel(rs.getString("mem_tel"));
				mem.setMem_email(rs.getString("mem_email"));
				mem.setMem_addr(rs.getString("mem_addr"));
				mem.setMem_genre(rs.getString("mem_genre"));
				mem.setMem_point(rs.getInt("mem_point"));
				mem.setMem_grade(rs.getInt("mem_grade"));
			}
		}catch (Exception e) {
			System.out.println("getMember �����߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return mem;
	}
	
	
	
	
	
	//6) ã�� ȸ���� ����=>ȸ���������ִ� �޼���� ����(sql������ �ٸ���)
	public boolean memberUpdate(MemberDTO mem) {
		boolean check=false;// ȸ������ ��������
		
		System.out.println("==memberUpdate() ����==");
		System.out.println("mem.getMem_id()=>"+mem.getMem_id());
		//-------------------------------------------------------
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			//-----------------------
			sql="update member set mem_passwd=?, mem_name=?, mem_nickname=?, mem_birth=?, mem_tel=?, mem_email=?, mem_addr=?, mem_genre=?, mem_point=?, mem_grade=? where mem_id=?";
			pstmt=con.prepareStatement(sql);
			
			pstmt.setString(1, mem.getMem_passwd());
			pstmt.setString(2, mem.getMem_name());
			pstmt.setString(3, mem.getMem_nickname());
			pstmt.setString(4, mem.getMem_birth());
			pstmt.setString(5, mem.getMem_tel());
			pstmt.setString(6, mem.getMem_email());
			pstmt.setString(7, mem.getMem_addr());
			pstmt.setString(8, mem.getMem_genre());
			pstmt.setInt(9, mem.getMem_point());
			pstmt.setInt(10, mem.getMem_grade());
			pstmt.setString(11, mem.getMem_id());
			
			int update1=pstmt.executeUpdate(); //��ȯ�� 1(����), 0(����)
			con.commit();
			System.out.println("update1(��� ���̺� ������ ��������)=>"+update1);
			if (update1 == 1) {
				check=true;
			}
		}catch (Exception e) {
			System.out.println("memberUpdate() �����߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt);
		}
		
		if (check==true) {
		//login���̺� update
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			//-----------------------
			sql="update login set log_passwd=?, log_nickname=?, log_point=?, log_grade=? where mem_id=?";
			pstmt=con.prepareStatement(sql);
			
			pstmt.setString(1, mem.getMem_passwd());
			pstmt.setString(2, mem.getMem_nickname());
			pstmt.setInt(3, mem.getMem_point());
			pstmt.setInt(4, mem.getMem_grade());
			pstmt.setString(5, mem.getMem_id());
			
			
			int update2=pstmt.executeUpdate(); //��ȯ�� 1(����), 0(����)
			con.commit();
			System.out.println("update2(�α��� ���̺� ������ ��������)=>"+update2);
			if (update2 == 1) {
				check=true;
			}
		}catch (Exception e) {
			System.out.println("loginUpdate() �����߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt);
		}
		}
		
		return check; //memberUpdate.jsp���� �޼����� ��ȯ��
	}
	
	//7) ȸ��Ż��
	
	public int beforeMemberDelete(String id) {
		int x=-1;
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			
			String sqlh="update h_table set mem_id='dropped' where mem_id=?";
			pstmt=con.prepareStatement(sqlh);
			pstmt.setString(1, id);
			int deleteh=pstmt.executeUpdate();
			System.out.println("Ż��� h_table ���̵� ó�� ���� ����=>"+deleteh);
			con.commit();
			
			String sqlhc="update h_comment set mem_id='dropped' where mem_id=?";
			pstmt=con.prepareStatement(sqlhc);
			pstmt.setString(1, id);
			int deletehc=pstmt.executeUpdate();
			System.out.println("Ż��� h_comment ���̵� ó�� ���� ����=>"+deletehc);
			con.commit();
			
			
			String sqlz="update z_table set mem_id='dropped' where mem_id=?";
			pstmt=con.prepareStatement(sqlz);
			pstmt.setString(1, id);
			int deletez=pstmt.executeUpdate();
			System.out.println("Ż��� z_table ���̵� ó�� ���� ����=>"+deletez);
			con.commit();
			
			String sqlzc="update z_comment set mem_id='dropped' where mem_id=?";
			pstmt=con.prepareStatement(sqlzc);
			pstmt.setString(1, id);
			int deletezc=pstmt.executeUpdate();
			System.out.println("Ż��� z_comment ���̵� ó�� ���� ����=>"+deletezc);
			con.commit();
			
			
			String sqlm="update m_table set mem_id='dropped' where mem_id=?";
			pstmt=con.prepareStatement(sqlm);
			pstmt.setString(1, id);
			int deletem=pstmt.executeUpdate();
			System.out.println("Ż��� m_table ���̵� ó�� ���� ����=>"+deletem);
			con.commit();
			
			String sqlmc="update m_comment set mem_id='dropped' where mem_id=?";
			pstmt=con.prepareStatement(sqlmc);
			pstmt.setString(1, id);
			int deletemc=pstmt.executeUpdate();
			System.out.println("Ż��� m_comment ���̵� ó�� ���� ����=>"+deletemc);
			con.commit();
			
			
			String sqlg="update g_table set mem_id='dropped' where mem_id=?";
			pstmt=con.prepareStatement(sqlg);
			pstmt.setString(1, id);
			int deleteg=pstmt.executeUpdate();
			System.out.println("Ż��� g_table ���̵� ó�� ���� ����=>"+deleteg);
			con.commit();
			
			String sqlgc="update g_comment set mem_id='dropped' where mem_id=?";
			pstmt=con.prepareStatement(sqlgc);
			pstmt.setString(1, id);
			int deletegc=pstmt.executeUpdate();
			System.out.println("Ż��� g_comment ���̵� ó�� ���� ����=>"+deletegc);
			con.commit();
			
			
			String sqlr="update r_table set mem_id='dropped' where mem_id=?";
			pstmt=con.prepareStatement(sqlr);
			pstmt.setString(1, id);
			int deleter=pstmt.executeUpdate();
			System.out.println("Ż��� r_table ���̵� ó�� ���� ����=>"+deleter);
			con.commit();
			
			String sqlrc="update r_comment set mem_id='dropped' where mem_id=?";
			pstmt=con.prepareStatement(sqlrc);
			pstmt.setString(1, id);
			int deleterc=pstmt.executeUpdate();
			System.out.println("Ż��� r_comment ���̵� ó�� ���� ����=>"+deleterc);
			con.commit();
			
			//��ũ�����̺����� ����
			String sqls="delete from scrap where mem_id=?";
			pstmt=con.prepareStatement(sqls);
			pstmt.setString(1, id);
			int deletes=pstmt.executeUpdate();
			System.out.println("Ż��� scrap ���� ���� ����=>"+deletes);
			con.commit();
			
			//�������̺����� ����
			String sqlstar="delete from star where mem_id=?";
			pstmt=con.prepareStatement(sqlstar);
			pstmt.setString(1, id);
			int deletestar=pstmt.executeUpdate();
			System.out.println("Ż��� star ���� ���� ����=>"+deletestar);
			con.commit();
			
			x=1;
		}catch (Exception e) {
			System.out.println("beforeMemberDelete() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt);
		}
		
		return x;
	}
	
	
	
	//select passwd from chmember where id='admin';
	//delete from chmember where id='admin'; //id �Ű����� �޾ƾ� ��
	public int memberDelete(String id, String passwd) {
		String dbpasswd=""; //DB�󿡼� ã�� ��ȣ�� ������ ����
		int x=-1; //ȸ��Ż������
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			sql="select log_passwd from login where mem_id=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, id);
			rs=pstmt.executeQuery();
			//��ȣ�� ã�Ҵٸ�
			if (rs.next()) {
				dbpasswd=rs.getString("log_passwd");
				System.out.println("dbpasswd=>"+dbpasswd);
				
				if (dbpasswd.equals(passwd)) {
					//���� ���� ��, ��۵� ���̵� dropped�� �ٲٱ�
					int beforeDelete=this.beforeMemberDelete(id);
					con.commit();
					System.out.println("beforeDelete=>"+beforeDelete);
					
					if (beforeDelete > 0) {
						pstmt=con.prepareStatement("delete from login where mem_id=?");
						pstmt.setString(1, id);
						int delete=pstmt.executeUpdate();
						System.out.println("delete(ȸ��Ż�� ��������)=>"+delete);
						con.commit();
						x=1; //ȸ��Ż�� ����
					}
					
				}else {
					x=0; //ȸ��Ż�� ����
				}
			}else { //��ȣ�� �������� �ʴ� ���
				x=-1;
			}
		}catch (Exception e) {
			System.out.println("memberDelete() �����߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}
	
	//����Ʈ, ��� ����ȭ
	public void syncPG(String mem_id) {
		
		int log_point, log_grade;
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			sql="select log_point, log_grade from login where mem_id=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, mem_id);
			rs=pstmt.executeQuery();
			if (rs.next()) {
				log_point=rs.getInt(1);
				log_grade=rs.getInt(2);
				con.commit();
				
				sql="update member set mem_point=?, mem_grade=? where mem_id=?";
				pstmt=con.prepareStatement(sql);
				pstmt.setInt(1, log_point);
				pstmt.setInt(2, log_grade);
				pstmt.setString(3, mem_id);
				
				int sync=pstmt.executeUpdate();
				con.commit();
				System.out.println("����Ʈ, ��� ����ȭ ���� ����(sync)=>"+sync+" //(1���� 0����)");
			}
		}catch (Exception e) {
			
		}finally {
			
		}
		
	}
	
	
	//ȸ������Ʈ
	
	public Hashtable pageList(String pageNum, int count) {

		Hashtable<String, Integer> pgList = new Hashtable<String, Integer>();

		int pageSize = 10; // numPerPage=>�������� �����ִ� �Խù� ��(=���ڵ� ��) default:10
		int blockSize = 10; // pagePerBlock=>���� �����ִ� �������� default:10

		// �Խ����� �� ó�� �����Ű�� ������ 1���������� ���->���� �ֱ��� ���� ������ ����
		if (pageNum == null) {
			pageNum = "1"; // default(������ 1�������� �������� �ʾƵ� ������� �ϱ� ����)
		}
		int currentPage = Integer.parseInt(pageNum); // "1"->1(=nowPage)(���� ������)
		// (1-1)*10+1=1, (2-1)*10+1=11, (3-1)*10+1=21
		int startRow = (currentPage - 1) * pageSize + 1; // ���� ���ڵ� ��ȣ
		int endRow = currentPage * pageSize; // 1*10=10, 2*10=20, 3*10=30
		int number = 0; // beginPerPage->���������� �����ϴ� �� ó���� ������ �Խù���ȣ
		System.out.println("���� ���ڵ� ��(count)=>" + count);
		number = count - (currentPage - 1) * pageSize;
		System.out.println(currentPage + "�������� number=>" + number);
		// ��1�� list.jsp ����¡ ó�� ����
		// 122/10=12.2+1.0=13.2=13
		int pageCount = count / pageSize + (count % pageSize == 0 ? 0 : 1);
		// 2. ���������� @@@@�߿�@@@@
		int startPage = 0;
		if (currentPage % blockSize != 0) { // ������ ���� 1~9, 11~19. 21~29 (10�� ����� �ƴ� ���)
			startPage = currentPage / blockSize * blockSize + 1; // ��輱 ������
		} else { // 10%10=0(10,20,30,40,,,)
					// ((10/10)-1)*10+1=1
			startPage = ((currentPage / blockSize) - 1) * blockSize + 1;
		}
		// ����������
		int endPage = startPage + blockSize - 1; // 1+10-1=10, 2+10-1=11
		System.out.println("startPage=>" + startPage + ", endPage=>" + endPage);
		// ������ �����ؼ� ��ũ �ɾ ���(������ ������ > �� ������ �� �� �� ��... ���ƾ� ��)
		// 11>10=>endPage=10
		if (endPage > pageCount)
			endPage = pageCount; // ������ ������=�� ������ �� �̵��� ������ֱ�
		// ����¡ ó���� ���� �����->Hashtable, HashMap=>ListAction�� ����->list.jsp
		pgList.put("pageSize", pageSize); // <->pgList(Ű��)("pageSize")
		pgList.put("blockSize", blockSize);
		pgList.put("currentPage", currentPage);
		pgList.put("startRow", startRow);
		pgList.put("endRow", endRow);
		pgList.put("count", count);
		pgList.put("number", number);
		pgList.put("startPage", startPage);
		pgList.put("endPage", endPage);
		pgList.put("pageCount", pageCount);

		return pgList;
	}
	
	//ȸ�� ��
	public int getMemberCount(String search, String searchtext) {
		
		int x=0;
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			if (search==null || search == "") {
				sql="select count(*) from login";
			}else {
				sql="select count(*) from login where " + search + " like '%" + searchtext + "%' ";
			}
			System.out.println("getMemberCount() �˻���=>"+sql);
			con.commit();
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			if (rs.next()) {
				x=rs.getInt(1);
				con.commit();
				System.out.println("�˻��� ȸ�� ��=>"+x);
			}
			con.commit();
		}catch (Exception e) {
			System.out.println("getMemberCount() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return x;
	}
	
	//ȸ�� ����Ʈ-��޼�
	public List<MemberDTO> getMembers(int start, int end, String search, String searchtext) {
		
		List<MemberDTO> memberList=null;
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			if (search==null || search=="") {
				sql="select * from (select login.*, rownum as rn from (select * from login order by log_grade desc, log_point desc) login) where rn >=? and rn <=?";
			}else {
				sql="select * from (select login.*, rownum as rn from (select * from login where " + search + " like '%" + searchtext + "%' order by log_grade desc, log_point desc) login) where rn >=? and rn <=?";
			}
			System.out.println("getMembers() �˻���=>"+sql);
			
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, start);
			pstmt.setInt(2, end);
			rs=pstmt.executeQuery();
			if (rs.next()) {
				memberList=new ArrayList(end);
				con.commit();
				do {
					MemberDTO member=new MemberDTO();
					member.setMem_id(rs.getString("mem_id"));
					member.setLog_passwd(rs.getString("log_passwd"));
					member.setLog_nickname(rs.getString("log_nickname"));
					member.setLog_point(rs.getInt("log_point"));
					member.setLog_grade(rs.getInt("log_grade"));
					memberList.add(member);
					con.commit();
				}while (rs.next());
			}
			con.commit();
		}catch (Exception e) {
			System.out.println("getMembers() ���� �߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return memberList;
	}
	
	//ȸ������Ż��
	public int memberDrop(String mem_id) {
		int x=-1; //ȸ��Ż������
		
		try {
			con=pool.getConnection();
			con.setAutoCommit(false);
			
			//���� ���� ��, ��۵� ���̵� dropped�� �ٲٱ�
			int beforeDelete=this.beforeMemberDelete(mem_id);
			con.commit();
			System.out.println("beforeDelete=>"+beforeDelete);
			if (beforeDelete > 0) {
				sql="delete from login where mem_id=?";
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, mem_id);
				int delete=pstmt.executeUpdate();
				System.out.println("delete(ȸ�� ���� ���� ����)=>"+delete);
				con.commit();
				x=1; //ȸ��Ż�� ����
			}
		}catch (Exception e) {
			System.out.println("memberDelete() �����߻�=>"+e);
		}finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}
	
	
	
}
