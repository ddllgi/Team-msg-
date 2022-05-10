package beans;

import java.sql.*;
import java.util.*;

public class HCommentDAO {

	private DBConnectionMgr pool = null; // 1. ���ᰴü����
	// ����
	private Connection con = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private String sql = "";

	public HCommentDAO() {

		try {
			pool = DBConnectionMgr.getInstance();
			System.out.println("pool=>" + pool);
		} catch (Exception e) {
			System.out.println("DB���� ����=>" + e);
		}
	}

	
	private HCommentDTO makeArticleFromResult() throws Exception {

		HCommentDTO article = new HCommentDTO();

		article.setH_cnumber(rs.getInt("h_cnumber"));
		article.setMem_id(rs.getString("mem_id"));
		article.setH_number(rs.getInt("h_number"));
		article.setH_cbody(rs.getString("h_cbody"));
		article.setH_cnickname(rs.getString("h_cnickname"));
		article.setH_cdate(rs.getTimestamp("h_cdate"));
		article.setH_cref(rs.getInt("h_cref"));
		article.setH_cre_step(rs.getInt("h_cre_step"));
		article.setH_cre_level(rs.getInt("h_cre_level"));

		return article;
	}

	public List<HCommentDTO> getComment(int h_number) {

		List<HCommentDTO> commentList = new ArrayList<HCommentDTO>();

		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			System.out.println("con=>" + con);
			sql = "select * from h_comment where h_number=? order by h_cref asc, h_cre_step asc, h_cdate asc";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, h_number);
			con.commit();
			rs = pstmt.executeQuery();

			if (rs.next()) {
				do {
					HCommentDTO comment = new HCommentDTO();
					comment = this.makeArticleFromResult();
					commentList.add(comment);
					con.commit();
				} while (rs.next());
			}
			con.commit();
		} catch (Exception e) {
			System.out.println("getComment() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return commentList;
	}

	//��� �� ���
	public List<HCommentDTO> getCommentCountForList(int start, int end) {

		List<HCommentDTO> commentCountList = new ArrayList<HCommentDTO>();
		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			System.out.println("con=>" + con);
			sql = "select count(*) from (select h_comment.*, rownum as rn from (select * from h_comment) h_comment) where rn >= ? and rn <= ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, start);
			pstmt.setInt(2, end);
			con.commit();
			rs = pstmt.executeQuery();

			if (rs.next()) {
				commentCountList=new ArrayList(end);
				do {
					HCommentDTO comment = new HCommentDTO();
					comment.setH_commentcount(rs.getInt(1));
					commentCountList.add(comment);
					con.commit();
				} while (rs.next());
			}
			con.commit();
		} catch (Exception e) {
			System.out.println("getCommentCountForList() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return commentCountList;
	}
	
	/*
	 * public void insertComment(HCommentDTO comment) {
	 * 
	 * int h_cnumber = comment.getH_cnumber(); int result = 0; int number = 0;
	 * String sql1 = "select max(h_cnumber) from h_comment"; sql =
	 * "insert into h_comment values(?, ?, ?, ?, ?, ?, ?, ?, ?)"; String sql2 =
	 * "update h_comment set h_cre_step=h_cre_step+1 where h_cref=? and h_cre_step>?"
	 * ; try { con = pool.getConnection(); con.setAutoCommit(false);
	 * System.out.println("con=>" + con);
	 * 
	 * if (h_cnumber != 0) { pstmt = con.prepareStatement(sql2); pstmt.setInt(1,
	 * comment.getH_cref()); pstmt.setInt(2, comment.getH_cre_step());
	 * pstmt.executeUpdate(); pstmt.close();
	 * comment.setH_cre_step(comment.getH_cre_step() + 1);
	 * comment.setH_cre_level(comment.getH_cre_level() + 1); con.commit(); }
	 * 
	 * pstmt = con.prepareStatement(sql1); rs = pstmt.executeQuery(); con.commit();
	 * if (rs.next()) { number = rs.getInt(1); con.commit(); } rs.close();
	 * pstmt.close();
	 * 
	 * if (h_cnumber==0) { comment.setH_cref(number); con.commit(); }
	 * 
	 * pstmt=con.prepareStatement(sql); pstmt.setInt(1, number); pstmt.setString(2,
	 * comment.getMem_id()); pstmt.setInt(3, comment.getH_number());
	 * pstmt.setString(4, comment.getH_cbody()); pstmt.setString(5,
	 * comment.getH_cnickname()); pstmt.setTimestamp(6, comment.getH_cdate());
	 * pstmt.setInt(7, comment.getH_cref()); pstmt.setInt(8,
	 * comment.getH_cre_step()); pstmt.setInt(9, comment.getH_cre_level());
	 * result=pstmt.executeUpdate(); con.commit();
	 * System.out.println("��۾��� ��������(result)=>"+result); } catch (Exception e) {
	 * System.out.println("insertComment() ���� �߻�=>"+e); } finally {
	 * pool.freeConnection(con, pstmt, rs); }
	 * 
	 * }
	 */

	// �Խ����� �۾��� �� �亯�� ����

	public void insertComment(HCommentDTO comment) {
		// 1. article->�űԱ����� �亯��(���� �Խù� ��ȣ)���� Ȯ��
		int h_cnumber = comment.getH_cnumber();
		int h_cref = comment.getH_cref();
		int h_cre_step = comment.getH_cre_step();
		int h_cre_level = comment.getH_cre_level();

		int number = 0; // �����͸� �����ϱ� ���� �Խù� ��ȣ(=new)
		System.out.println("insertComment()�޼��� ���� h_cnumber=>" + h_cnumber);
		System.out.println("h_cref=>" + h_cref + ", h_cre_step=>" + h_cre_step + ", h_cre_level=>" + h_cre_level);

		int point, grade;
		
		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			sql = "select max(h_cnumber) from h_comment";
			// System.out.println("insertComment()�� ù ��° sql=>"+sql);
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				number = rs.getInt(1) + 1; // �ִ밪+1
				System.out.println("��� ��ȣ(number)=>" + number);
			} else {
				number = 1;
			}
			con.commit();
			// �亯���̶��(�Խñ۹�ȣ�� ����̸鼭 1 �̻��� ���)
			if (h_cnumber != 0) {
				// ���� �׷��ȣ�� ������ �����鼭(ref=?) ������ step���� ū �Խù�(re_step>?)�� ã�Ƽ� �� step �ϳ� ����
				// @@----->�������ѳ��� �ڱⰡ 1�� �ż� ������ ����

				System.out.println("update ���� h_cre_step=>" + h_cre_step);

				// h_cref�� ���� ��۵� �� step���� ���� ���� �� ã�Ƽ� �װͺ��� 1 ũ�� ����� ����
				String sql2 = "select max(h_cre_step) from h_comment where h_cref=?";
				pstmt = con.prepareStatement(sql2);
				pstmt.setInt(1, h_cref);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					h_cre_step = rs.getInt(1) + 1;
					System.out.println("h_cre_step�� �ִ�+1=>" + h_cre_step);
				} else {
					h_cre_step = h_cre_step + 1;
				}
				h_cre_level = h_cre_level + 1;
				System.out.println("update ���� h_cre_step=>" + h_cre_step + ", h_cre_level=>" + h_cre_level);
				con.commit();

				/*
				 * sql="update h_comment set h_cre_step=h_cre_step+1 where h_cref=? and h_cre_step>?"
				 * ;
				 * 
				 * System.out.println("insertComment()�� sql=>"+sql);
				 * pstmt=con.prepareStatement(sql); pstmt.setInt(1, h_cref); pstmt.setInt(2,
				 * h_cre_step); int update=pstmt.executeUpdate(); con.commit();
				 * System.out.println("��ۼ�������(update)=>"+update+" //(1���� 0����)"); //1���� 0����
				 * System.out.println("update �ϰ� ������ h_cre_step()=>"+h_cre_step);
				 */
				// �亯��
				// h_cre_step=h_cre_step + 1;

			} else { // �űԱ��̶�� num=0
				h_cref = number;
				h_cre_step = 0;
				h_cre_level = 0;
				con.commit();
			}
			sql = "insert into h_comment(h_cnumber, mem_id, h_number, h_cbody, h_cnickname, h_cdate, ";
			sql += " h_cref, h_cre_step, h_cre_level) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, number); // article.getNum()�� �ƴϾ���
			pstmt.setString(2, comment.getMem_id());
			pstmt.setInt(3, comment.getH_number());
			pstmt.setString(4, comment.getH_cbody());
			pstmt.setString(5, comment.getH_cnickname());
			pstmt.setTimestamp(6, comment.getH_cdate());

			pstmt.setInt(7, h_cref);
			pstmt.setInt(8, h_cre_step);
			pstmt.setInt(9, h_cre_level);

			int insert = pstmt.executeUpdate();
			con.commit();
			System.out.println("��۾��� ��������(insert)=>" + insert + " //(1���� 0����)");
			
			if (insert > 0) {
				String sql3="update login set log_point=log_point+5 where mem_id=?";
				pstmt=con.prepareStatement(sql3);
				pstmt.setString(1, comment.getMem_id());
				
				int pointup=pstmt.executeUpdate();
				con.commit();
				System.out.println("��� �ۼ� ����Ʈ ��������(pointup)=>"+pointup);
				
				if (pointup > 0) {
					String sql4 = "select log_point, log_grade from login where mem_id=?";
					pstmt = con.prepareStatement(sql4);
					pstmt.setString(1, comment.getMem_id());
					rs = pstmt.executeQuery();
					if (rs.next()) {
						point = rs.getInt(1);
						grade = rs.getInt(2);
						System.out.println("���� ����Ʈ(point)=>"+point+", ���(grade)=>"+grade);
						
						String sql5="";
						if (point < 100) {
							sql5="update login set log_grade=1 where mem_id=?";
						}else if (point >= 100 && point < 500) {
							sql5="update login set log_grade=2 where mem_id=?";
						}else if (point >= 500 && point < 2000) {
							sql5="update login set log_grade=3 where mem_id=?";
						}else if (point >= 2000 && point < 10000) {
							sql5="update login set log_grade=4 where mem_id=?";
						}else if (point >= 10000) {
							sql5="update login set log_grade=5 where mem_id=?";
						}
						System.out.println("sql5=>"+sql5);
						pstmt=con.prepareStatement(sql5);
						pstmt.setString(1, comment.getMem_id());
							
						int gradeup=pstmt.executeUpdate();
						con.commit();
						System.out.println("��� �ۼ� �� ��� ��������(gradeup)=>"+gradeup);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("insertComment()�޼��� ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
	}

	// ��� ����

	// ��� ����->���̺��� �����ϴ� �� �ƴ϶� '������ ����Դϴ�' �� ����@@@
	public int deleteComment(int h_cnumber, String mem_id) {

		int x = -1;
		int point, grade;
		
		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			con.commit();

			sql = "delete from h_comment where h_cnumber=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, h_cnumber);

			int delete = pstmt.executeUpdate();
			con.commit();
			System.out.println("��� ���� ��������=>" + delete + " //(1���� 0����)");
			
			if (delete > 0) {
				String sql4="update login set log_point=log_point-5 where mem_id=?";
				pstmt=con.prepareStatement(sql4);
				pstmt.setString(1, mem_id);
				
				int pointdown=pstmt.executeUpdate();
				con.commit();
				System.out.println("��� ������ ����Ʈ ȸ������(pointdown)=>"+pointdown);
				
				if (pointdown > 0) {
					String sql5 = "select log_point, log_grade from login where mem_id=?";
					pstmt = con.prepareStatement(sql5);
					pstmt.setString(1, mem_id);
					rs = pstmt.executeQuery();
					if (rs.next()) {
						point = rs.getInt(1);
						grade = rs.getInt(2);
						System.out.println("���� ����Ʈ(point)=>"+point+", ���(grade)=>"+grade);
						
						String sql6="";
						if (point < 100) {
							sql4="update login set log_grade=1 where mem_id=?";
						}else if (point >= 100 && point < 500) {
							sql4="update login set log_grade=2 where mem_id=?";
						}else if (point >= 500 && point < 2000) {
							sql4="update login set log_grade=3 where mem_id=?";
						}else if (point >= 2000 && point < 10000) {
							sql4="update login set log_grade=4 where mem_id=?";
						}else if (point >= 10000) {
							sql4="update login set log_grade=5 where mem_id=?";
						}
						System.out.println("sql6=>"+sql6);
						pstmt=con.prepareStatement(sql6);
						pstmt.setString(1, mem_id);
							
						int gradeup=pstmt.executeUpdate();
						con.commit();
						System.out.println("��� ���� �� ��� ��������(gradeup)=>"+gradeup);
					}
				}
			}
			
			x = 1;

		} catch (Exception e) {
			System.out.println("deleteComment() �����߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}

	// �ۻ���->������ ����Դϴ�
	public int upDeleteComment(int h_cnumber, int h_cref, int h_cre_level, String h_cmem_id) {

		int x = -1; // �Խù� �������� 1���� 0����
		int point, grade;
		
		try {
			con = pool.getConnection();
			con.setAutoCommit(false);
			con.commit();
			
			String sql1="select * from h_comment where h_cref=? and h_cre_level>?";
			pstmt=con.prepareStatement(sql1);
			pstmt.setInt(1, h_cref);
			pstmt.setInt(2, h_cre_level);
			rs=pstmt.executeQuery();
			
			if(rs.next()) { //���۵��� �ִ� ����̶��
				sql = "update h_comment set h_cnickname=' ', h_cbody='������ ����Դϴ�.&nbsp;' where h_cnumber=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, h_cnumber);
			}else {
				sql = "delete from h_comment where h_cnumber=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, h_cnumber);
			}
			
			int update = pstmt.executeUpdate();
			con.commit();
			System.out.println("��ۻ���(����) ���� ����(update)=>" + update + " //(1���� 0����)");
			
			if (update > 0) {
				String sql4="update login set log_point=log_point-5 where mem_id=?";
				pstmt=con.prepareStatement(sql4);
				pstmt.setString(1, h_cmem_id);
				
				int pointdown=pstmt.executeUpdate();
				con.commit();
				System.out.println("��� ������ ����Ʈ ȸ������(pointdown)=>"+pointdown);
				
				if (pointdown > 0) {
					String sql5 = "select log_point, log_grade from login where mem_id=?";
					pstmt = con.prepareStatement(sql5);
					pstmt.setString(1, h_cmem_id);
					rs = pstmt.executeQuery();
					if (rs.next()) {
						point = rs.getInt(1);
						grade = rs.getInt(2);
						System.out.println("���� ����Ʈ(point)=>"+point+", ���(grade)=>"+grade);
						
						String sql6="";
						if (point < 100) {
							sql6="update login set log_grade=1 where mem_id=?";
						}else if (point >= 100 && point < 500) {
							sql6="update login set log_grade=2 where mem_id=?";
						}else if (point >= 500 && point < 2000) {
							sql6="update login set log_grade=3 where mem_id=?";
						}else if (point >= 2000 && point < 10000) {
							sql6="update login set log_grade=4 where mem_id=?";
						}else if (point >= 10000) {
							sql6="update login set log_grade=5 where mem_id=?";
						}
						System.out.println("sql6=>"+sql6);
						pstmt=con.prepareStatement(sql6);
						pstmt.setString(1, h_cmem_id);
							
						int gradeup=pstmt.executeUpdate();
						con.commit();
						System.out.println("��� ���� �� ��� ��������(gradeup)=>"+gradeup);
					}
				}
			}
			
			
			x = 1; // ��������
		} catch (Exception e) {
			System.out.println("upDeleteComment() ���� �߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}

	// ��� ��
	public int getCommentCount(int h_number) { // getmemberCount()->MemberDAO���� �ۼ�
		int x = 0;
		try {
			con = pool.getConnection();
			System.out.println("con=>" + con);
			sql = "select count(*) from h_comment where h_number=? and h_cbody!='������ ����Դϴ�.&nbsp;'";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, h_number);
			rs = pstmt.executeQuery();
			if (rs.next()) { // �����ִ� ����� �ִٸ�
				x = rs.getInt(1); // ������=rs.get�ڷ���(�ʵ�� �Ǵ� �ε�����ȣ)=>���⼭�� �ʵ��X(�ʵ���� ���� ����)
			}
		} catch (Exception e) {
			System.out.println("getCommentCount() �����߻�=>" + e);
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return x;
	}

}
