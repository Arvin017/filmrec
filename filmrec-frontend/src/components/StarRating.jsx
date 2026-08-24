import { useState } from 'react'

/** A simple 1-5 star clickable rating control. */
export default function StarRating({ value = 0, onChange, disabled = false }) {
  const [hovered, setHovered] = useState(0)

  const display = hovered || value

  return (
    <div className="star-rating" onMouseLeave={() => setHovered(0)}>
      {[1, 2, 3, 4, 5].map((star) => (
        <button
          key={star}
          type="button"
          disabled={disabled}
          className={`star ${star <= display ? 'star-filled' : ''}`}
          onMouseEnter={() => setHovered(star)}
          onClick={() => onChange?.(star)}
          aria-label={`Rate ${star} stars`}
        >
          ★
        </button>
      ))}
    </div>
  )
}
