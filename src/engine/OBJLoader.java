package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class OBJLoader {
	Vertex[] m_vertices;
	int[] m_indices;
	
	public OBJLoader(String filePath)
	{
		BufferedReader br = null;
		
		// Try to load model
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<Vertex> tempVertices = new ArrayList<Vertex>();
		ArrayList<Integer> tempIndices = new ArrayList<Integer>();
		ArrayList<Vector> tempTexCoords = new ArrayList<Vector>();
		
		String st;
		try {
			Random random = new Random();
			
			int j = 0;
			while((st = br.readLine()) != null)
			{
				String[] splitString = st.split(" ");
				
				// Vertex
				if(splitString[0].matches("v"))
				{
					// v  x y z
					Vertex v = new Vertex(
						new Vector(
							Float.parseFloat(splitString[1]),
							Float.parseFloat(splitString[2]),
							Float.parseFloat(splitString[3])
						),
						new Vector(random.nextInt() % 255, random.nextInt() % 255, random.nextInt() % 255),
						new Vector(random.nextFloat(), random.nextFloat())
					);
					
					tempVertices.add(v);
				}
				// Texture coordinates
				else if(splitString[0].matches("vt"))
				{
					tempTexCoords.add(
						new Vector(
							Float.parseFloat(splitString[1]), 
							Float.parseFloat(splitString[2])
						)
					);
				}
				// Index
				else if(splitString[0].matches("f"))
				{
					for(int i = 1; i < splitString.length; i++)
					{
						if(i == 4)
						{
							tempIndices.add(tempIndices.get(tempIndices.size()-3));
							tempIndices.add(tempIndices.get(tempIndices.size()-2));
						}
						
						// f  v1/t1/n1 v2/t2/n2 v3/t3/n3
						String[] indicesSplitString = splitString[i].split("/");
						
						int vertIndex = Integer.parseInt(indicesSplitString[0])-1;
						int texIndex = Integer.parseInt(indicesSplitString[1])-1;
						
						tempIndices.add(new Integer(vertIndex));
						
						// Set texture coordinates
						tempVertices.get(vertIndex).m_texCoord.Set(tempTexCoords.get(texIndex).x, tempTexCoords.get(texIndex).y, 0.0f);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Fill arrays with elements from arraylists
		m_vertices = new Vertex[tempVertices.size()];
		m_indices = new int[tempIndices.size()];
		
		for(int i = 0; i < tempVertices.size(); i++)
		{
			m_vertices[i] = tempVertices.get(i);
		}
		
		for(int i = 0; i < tempIndices.size(); i++)
		{
			m_indices[i] = tempIndices.get(i);
		}
		FlipFaces();	// It seems like I'm not ordering the vertices counter-clockwise? Not sure how, but this fixes it anyways.
		
		// Print stats
		System.out.println("Vertex count: " + m_vertices.length);
		System.out.println("Index count: " + m_indices.length);
		System.out.println("Triangles to render: " + m_indices.length / 3);
	}
	
	public void FlipFaces()
	{
		for(int i = 0; i < m_indices.length; i += 3)
		{
			int temp = m_indices[i + 2];
			m_indices[i + 2] = m_indices[i + 1];
			m_indices[i + 1] = temp;
		}
	}
	
	public Vertex[] GetVertices() { return m_vertices; }
	
	public int[] GetIndices() { return m_indices; }
}
