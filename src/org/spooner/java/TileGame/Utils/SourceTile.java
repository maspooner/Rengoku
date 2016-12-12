package org.spooner.java.TileGame.Utils;

public class SourceTile implements Comparable<SourceTile>{
	//members
	private int[][] rgbMatrix;
	//constructors
	protected SourceTile(int[][] rgbMatrix){
		this.rgbMatrix = rgbMatrix;
	}
	//methods
	public int getRGB(int i, int j){ return rgbMatrix[i][j]; }
	private int getAverageRGB(){
		int sum = 0;
		for(int[] ia : rgbMatrix){
			for(int i : ia){
				sum += i;
			}
		}
		double av = sum / (rgbMatrix.length * rgbMatrix[0].length);
		return (int) av;
	}
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof SourceTile)) return false;
			SourceTile that = (SourceTile) obj;
			for(int i = 0; i < rgbMatrix.length; i++){
				for(int j = 0; j < rgbMatrix[i].length; j++){
					//not equal if color at coord doesn't match
					if(this.rgbMatrix[i][j] != that.rgbMatrix[i][j]){
						return false;
					}
				}
			}
		return true;
	}
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash += 2 * rgbMatrix.hashCode();
		return hash;
	}
	@Override
	public int compareTo(SourceTile st){
		return this.getAverageRGB() - st.getAverageRGB();
	}
}
