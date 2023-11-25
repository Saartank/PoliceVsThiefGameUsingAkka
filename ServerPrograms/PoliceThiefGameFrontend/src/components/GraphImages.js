const GraphImages = () => {
  return (
    <div className="flex justify-center items-center space-x-4">
      <div className="image-container text-center">
        <p className="p-4 text-lg">Original Graph - Police's View</p>
        <img
          src="/graphs/NetGraph_19-11-23-11-55-24.ngs.png"
          alt="First Image"
          className="object-cover"
          style={{ height: "630px" }} // Set a fixed height for the image
        />
      </div>

      <div className="image-container text-center">
        <p className="p-4 text-lg">Perturbed Graph - Thief's View</p>
        <img
          src="/graphs/NetGraph_19-11-23-11-55-24.ngs.perturbed.png"
          alt="Second Image"
          className="object-cover"
          style={{ height: "630px" }} // Set the same fixed height for the image
        />
      </div>
    </div>
  );
};

export default GraphImages;
