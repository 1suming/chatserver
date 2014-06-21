package gotest;

import com.chatserver.client.AesHelper;
import com.google.protobuf.InvalidProtocolBufferException;
import pb.*;

public class GoTest {
	public static void main(String[] args) {
		// ����client��������
		com.chatserver.client.TcpHelper client = new com.chatserver.client.TcpHelper();
		boolean re = client.connect("127.0.0.1", 8989);
		if (!re) {
			System.out.println("connect error\n");
			return;
		}
		
		// ��ʼ����¼��
		Pb.PbClientLogin.Builder clientLoginBuilder = Pb.PbClientLogin.newBuilder();
		clientLoginBuilder.setUuid("hello�� ����һ��GG, ����дһ��java����");
		clientLoginBuilder.setVersion(3.14f);
		clientLoginBuilder.setTimestamp((int)(System.currentTimeMillis()/1000));
		
		// ���л���¼����������
		byte[] sendData = marshalPbClientLogin(clientLoginBuilder);
		re = client.send(sendData);
		if (!re) {
			System.out.println("send error\n");
			return;
		}
		
		// ��ȡ����
		byte[] readData = new byte[1024];
		int readSize = client.read(readData);
		if (-1 == readSize) {
			System.out.println("read error\n");
			return;
		}
		
		// �ٶ���ȡ�������ǵ�¼������
		byte[] tempData = new byte[readSize];
		System.arraycopy(readData, 0, tempData, 0, readSize);
		Pb.PbClientLogin clientLogin = unmarshalPbClientLogin(tempData);
		if (null == clientLogin) {
			System.out.println("unmarshalPbClientLogin error\n");
			return;
		}
		System.out.println(clientLogin.getUuid());
		System.out.println(clientLogin.getVersion());
		System.out.println(clientLogin.getTimestamp());
		
		// �ر�����
		client.close();
	}
	
	// ��pb�ṹ���л�����aes����
	public static byte[] marshalPbClientLogin(Pb.PbClientLogin.Builder clientLoginBuilder) {
		AesHelper aes = new AesHelper("12345abcdef67890");
		Pb.PbClientLogin info = clientLoginBuilder.build();
		return aes.encrypt(info.toByteArray());
	}
	
	// ��aes���ܣ��ٷ����л�Ϊpb�ṹ
	public static Pb.PbClientLogin unmarshalPbClientLogin(byte[] data) {
		AesHelper aes = new AesHelper("12345abcdef67890");
		byte[] decrypted = aes.decrypt(data);
		
		Pb.PbClientLogin rev = null;
		try {
			rev = Pb.PbClientLogin.parseFrom(decrypted);
		} catch (InvalidProtocolBufferException e) {
			return null;
		}
		return rev;
	}
}
